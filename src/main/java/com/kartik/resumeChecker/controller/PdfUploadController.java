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

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@Authenticate
@RequestMapping("/pdf")
public class PdfUploadController {

    private final PdfDocumentRepository repository;
    private final FileStorageService fileStorageService;

    @Autowired
    public PdfUploadController(PdfDocumentRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

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

            String uuid = fileStorageService.generateUuid();

            String filePath = fileStorageService.storeFile(file, uuid);

            PdfDocument document = new PdfDocument();
            document.setFilename(file.getOriginalFilename());
            document.setUuid(uuid);
            document.setUserEmail(user.get("email"));
            document.setContentType(file.getContentType());
            document.setFilePath(filePath);
            document.setUploadDate(LocalDateTime.now());

            repository.save(document);
            String boundary = UUID.randomUUID().toString();
            byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();

            List<byte[]> byteSections = new ArrayList<>();

            byteSections.add(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            byteSections.add(("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n").getBytes(StandardCharsets.UTF_8));
            byteSections.add(("Content-Type: application/pdf\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            byteSections.add(fileBytes);
            byteSections.add("\r\n".getBytes(StandardCharsets.UTF_8));
            byteSections.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            int totalLength = byteSections.stream().mapToInt(b -> b.length).sum();
            byte[] finalBody = new byte[totalLength];
            int pos = 0;
            for (byte[] part : byteSections) {
                System.arraycopy(part, 0, finalBody, pos, part.length);
                pos += part.length;
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/predict"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(BodyPublishers.ofByteArray(finalBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return ResponseEntity.status(response.statusCode()).body(response.body());

        }
        catch (ConnectException e)
        {
            return ResponseEntity.internalServerError().body("AI PDF Scanner API issue!");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uuid) {
        try {
            PdfDocument document = repository.findByUuid(uuid)
                    .orElseThrow(() -> new RuntimeException("File not found with UUID: " + uuid));

            byte[] fileData = fileStorageService.readFile(document.getFilePath());

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