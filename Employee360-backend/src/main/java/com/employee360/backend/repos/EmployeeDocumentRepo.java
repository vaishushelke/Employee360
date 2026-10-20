
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.EmployeeDocument;
import com.employee360.backend.enums.DocumentType;

@Repository
public interface EmployeeDocumentRepo
        extends JpaRepository<EmployeeDocument, Long> {

    // Get documents uploaded by a specific employee
    List<EmployeeDocument> findByEmployeeUsernameOrderByUploadedAtDesc(
            String employeeUsername);

    // Get documents by employee and document type
    List<EmployeeDocument> findByEmployeeUsernameAndDocumentType(
            String employeeUsername,
            DocumentType documentType);
}
