
package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

import com.employee360.backend.dto.response.EmployeeDocumentResponse;
import com.employee360.backend.entities.EmployeeDocument;
import com.employee360.backend.repos.EmployeeDocumentRepo;
import com.employee360.backend.services.EmployeeDocumentService;

@RestController
@RequestMapping("/api/documents")
public class EmployeeDocumentController {

    private final EmployeeDocumentService documentService;
    private final EmployeeDocumentRepo documentRepo;

    public EmployeeDocumentController(
            EmployeeDocumentService documentService,
            EmployeeDocumentRepo documentRepo) {
        this.documentService = documentService;
        this.documentRepo = documentRepo;
    }

    // UPLOAD DOCUMENT
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentName") String documentName,
            @RequestParam("documentType") String documentType,
            Authentication authentication) {

        EmployeeDocumentResponse response =
                documentService.uploadDocument(
                        file,
                        documentName,
                        documentType,
                        authentication.getName());

        return ResponseEntity.status(201).body(response);
    }

    // GET MY DOCUMENTS
    @GetMapping
    public ResponseEntity<List<EmployeeDocumentResponse>> getMyDocuments(
            Authentication authentication) {

        return ResponseEntity.ok(
                documentService.getMyDocuments(authentication.getName()));
    }

    // DOWNLOAD DOCUMENT
    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long documentId,
            Authentication authentication) {

        byte[] fileData = documentService.downloadDocument(
                documentId, authentication.getName());

        EmployeeDocument document = documentRepo.findById(documentId)
                .orElseThrow();

        String encodedFilename = UriUtils.encode(
                document.getDocumentName(), StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(encodedFilename)
                        .build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(fileData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }

    // DELETE DOCUMENT
    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long documentId,
            Authentication authentication) {

        documentService.deleteDocument(
                documentId, authentication.getName());

        return ResponseEntity.ok("Document deleted successfully");
    }
}