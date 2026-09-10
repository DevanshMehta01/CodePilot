package codepilot.controller;

import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import codepilot.dto.document.DocumentResponse;
import codepilot.service.DocumentService;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<List<DocumentResponse>> uploadFiles(
            @PathVariable Long projectId,
            @RequestParam("files") List<MultipartFile> files) {

        List<DocumentResponse> uploaded = documentService.uploadFiles(projectId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getFiles(@PathVariable Long projectId) {
        return ResponseEntity.ok(documentService.getFilesByProject(projectId));
    }
}
