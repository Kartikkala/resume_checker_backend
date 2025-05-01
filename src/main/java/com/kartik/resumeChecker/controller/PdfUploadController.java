package com.kartik.resumeChecker.controller;

import com.kartik.authentication.annotations.Authenticate;
import com.kartik.resumeChecker.model.PdfDocument;
import com.kartik.resumeChecker.repository.PdfDocumentRepository;
import com.kartik.resumeChecker.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/pdf")
public class PdfUploadController {

    private final PdfDocumentRepository repository;
    private final FileStorageService fileStorageService;

    @Autowired
    public PdfUploadController(PdfDocumentRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    @Authenticate
    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(
            @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be empty");
        }

        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDFs are allowed");
        }

        try {
            HashMap<String, String> user = (HashMap<String, String>)request.getAttribute("user");
            // Generate a UUID for the file
            String uuid = fileStorageService.generateUuid();

            // Store the file in the filesystem
            String filePath = fileStorageService.storeFile(file, uuid);


            // Store metadata in the database
            PdfDocument document = new PdfDocument();
            document.setFilename(file.getOriginalFilename());
            document.setUuid(uuid);
            document.setUserEmail(user.get("email"));
            document.setContentType(file.getContentType());
            document.setFilePath(filePath);
            document.setUploadDate(LocalDateTime.now());

            repository.save(document);

            return ResponseEntity.ok("PDF uploaded successfully with UUID: " + uuid);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uuid) {
        try {
            // Find the document in the database
            PdfDocument document = repository.findByUuid(uuid)
                    .orElseThrow(() -> new RuntimeException("File not found with UUID: " + uuid));

            // Read the file from the filesystem
            byte[] fileData = fileStorageService.readFile(document.getFilePath());

            // Create a resource from the file data
            ByteArrayResource resource = new ByteArrayResource(fileData);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/info/{uuid}")
    public ResponseEntity<?> getFileInfo(@PathVariable String uuid) {
        return repository.findByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteFile(@PathVariable String uuid) {
        try {
            PdfDocument document = repository.findByUuid(uuid)
                    .orElseThrow(() -> new RuntimeException("File not found with UUID: " + uuid));

            // Delete the file from the filesystem
            boolean deleted = fileStorageService.deleteFile(document.getFilePath());

            if (deleted) {
                // Delete the metadata from the database
                repository.delete(document);
                return ResponseEntity.ok("File deleted successfully");
            } else {
                return ResponseEntity.internalServerError().body("Could not delete file from filesystem");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error deleting file: " + e.getMessage());
        }
    }
}