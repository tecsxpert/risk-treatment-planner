package com.risk.controller;

import com.risk.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Tag(name = "Files", description = "Multipart upload and download by stored id prefix")
@SecurityRequirement(name = OpenApiConfig.BEARER_JWT)
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file", description = "Stores the file under a UUID-based name; max size 10 MB. Returns the UUID used as the file key.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "UUID key for the stored file (plain text body)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"))),
            @ApiResponse(responseCode = "400", description = "No file in request",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "No file selected"))),
            @ApiResponse(responseCode = "413", description = "File larger than 10 MB",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "File too large (max 10MB)"))),
            @ApiResponse(responseCode = "500", description = "Disk or IO error",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "File upload failed")))
    })
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "File to store", required = true)
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File too large (max 10MB)");
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String uuid = UUID.randomUUID().toString();
        String storedFilename = uuid + extension;
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(storedFilename);
            file.transferTo(filePath);
            return ResponseEntity.ok(uuid);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/files/{id}")
    @Operation(summary = "Download file by id", description = "Finds the first file whose name starts with the given id (UUID prefix) and returns its bytes with detected content type.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File bytes",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "No matching file", content = @Content),
            @ApiResponse(responseCode = "500", description = "Read error",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class, example = "File download failed")))
    })
    public ResponseEntity<?> getFile(
            @Parameter(description = "UUID prefix of the stored filename", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
            @PathVariable String id) {
        File dir = new File(uploadDir);
        File[] matches = dir.listFiles((d, name) -> name.startsWith(id));
        if (matches == null || matches.length == 0) {
            return ResponseEntity.notFound().build();
        }
        File file = matches[0];
        try {
            String mimeType = Files.probeContentType(file.toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File download failed");
        }
    }
}
