package com.kartik.resumeChecker.repository;

import com.kartik.resumeChecker.model.PdfDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {

    Optional<PdfDocument> findByUuid(String uuid);

    List<PdfDocument> findByUserEmail(String userEmail);
}