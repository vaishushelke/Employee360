
package com.employee360.backend.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.employee360.backend.enums.DocumentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.dto.response.EmployeeDocumentResponse;
import com.employee360.backend.entities.EmployeeDocument;
import com.employee360.backend.repos.EmployeeDocumentRepo;
import com.employee360.backend.services.EmployeeDocumentService;

@Service
@Transactional
public class EmployeeDocumentDao implements EmployeeDocumentService {

    private final EmployeeDocumentRepo documentRepo;

    @Value("${employee.documents.storage-path:uploads/employee-documents}")
    private String storagePath;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "png", "jpg", "jpeg", "doc", "docx");

    public EmployeeDocumentDao(EmployeeDocumentRepo documentRepo) {
        this.documentRepo = documentRepo;
    }

    @Override
    public EmployeeDocumentResponse uploadDocument(
            MultipartFile file,
            String documentName,
            String documentType,
            String username) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Please select a file");
        }

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        if (documentName == null || documentName.isBlank()
                || documentType == null || documentType.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Document name and type are required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Maximum file size is 5 MB");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid filename");
        }

        String safeOriginalName = Paths.get(originalFilename)
                .getFileName()
                .toString();

        int dotIndex = safeOriginalName.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == safeOriginalName.length() - 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File extension is required");
        }

        String extension = safeOriginalName
                .substring(dotIndex + 1)
                .toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Allowed file types: PDF, PNG, JPG, JPEG, DOC and DOCX");
        }

        Path directory = Paths.get(storagePath)
                .toAbsolutePath()
                .normalize();

        String storedFilename = UUID.randomUUID() + "." + extension;
        Path destination = directory.resolve(storedFilename).normalize();

        if (!destination.startsWith(directory)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Files.createDirectories(directory);
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to store the document");
        }

        try {
            DocumentType type = DocumentType.valueOf(
                    documentType.trim().toUpperCase()
            );

            EmployeeDocument document = new EmployeeDocument(
                    username.trim(),
                    documentName.trim(),
                    type,
                    destination.toString()
            );

            EmployeeDocument savedDocument = documentRepo.save(document);

            return toResponse(savedDocument);

        } catch (IllegalArgumentException exception) {
            try {
                Files.deleteIfExists(destination);
            } catch (IOException ignored) {
                // Keep the original validation error.
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid document type. Allowed types: RESUME, ID_PROOF, CERTIFICATE, HEALTH, OTHER"
            );

        } catch (RuntimeException exception) {
            try {
                Files.deleteIfExists(destination);
            } catch (IOException ignored) {
                // Keep the original database error.
            }

            throw exception;
        }
        }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentResponse> getMyDocuments(String username) {

        validateUsername(username);

        return documentRepo
                .findByEmployeeUsernameOrderByUploadedAtDesc(username.trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId, String username) {

        EmployeeDocument document = findOwnedDocument(documentId, username);

        try {
            Path path = Paths.get(document.getFilePath())
                    .toAbsolutePath()
                    .normalize();

            if (!Files.isRegularFile(path)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document file not found");
            }

            return Files.readAllBytes(path);

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read the document");
        }
    }

    @Override
    public void deleteDocument(Long documentId, String username) {

        EmployeeDocument document = findOwnedDocument(documentId, username);

        Path path = Paths.get(document.getFilePath())
                .toAbsolutePath()
                .normalize();

        try {
            Files.deleteIfExists(path);
            documentRepo.delete(document);
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to delete the document file");
        }
    }

    private EmployeeDocument findOwnedDocument(
            Long documentId, String username) {

        validateUsername(username);

        if (documentId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Document ID is required");
        }

        EmployeeDocument document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document not found"));

        if (!document.getEmployeeUsername().equals(username.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another employee's document");
        }

        return document;
    }

    private void validateUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
    }

    private EmployeeDocumentResponse toResponse(EmployeeDocument document) {

    	return new EmployeeDocumentResponse(
    	        document.getId(),
    	        document.getDocumentName(),
    	        document.getDocumentType().name(),
    	        document.getUploadedAt());
    }
}
