
package com.employee360.backend.dto.response;

import java.time.LocalDateTime;

public class EmployeeDocumentResponse {

    private Long id;
    private String documentName;
    private String documentType;
    private LocalDateTime uploadedAt;

    public EmployeeDocumentResponse() {
    }

    public EmployeeDocumentResponse(
            Long id,
            String documentName,
            String documentType,
            LocalDateTime uploadedAt) {

        this.id = id;
        this.documentName = documentName;
        this.documentType = documentType;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
