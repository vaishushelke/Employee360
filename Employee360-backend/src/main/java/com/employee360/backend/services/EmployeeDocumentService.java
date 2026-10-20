
package com.employee360.backend.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.employee360.backend.dto.response.EmployeeDocumentResponse;

public interface EmployeeDocumentService {

    // Upload a document for the logged-in employee
    EmployeeDocumentResponse uploadDocument(
            MultipartFile file,
            String documentName,
            String documentType,
            String username);

    // Get documents belonging to the logged-in employee
    List<EmployeeDocumentResponse> getMyDocuments(String username);

    // Download a document belonging to the logged-in employee
    byte[] downloadDocument(Long documentId, String username);

    // Delete a document belonging to the logged-in employee
    void deleteDocument(Long documentId, String username);
}
