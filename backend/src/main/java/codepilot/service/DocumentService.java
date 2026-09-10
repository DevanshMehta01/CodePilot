package codepilot.service;

import org.springframework.stereotype.Service; 
import org.springframework.web.multipart.MultipartFile;

import codepilot.dto.document.DocumentResponse;
import codepilot.entity.Document;
import codepilot.entity.Project;
import codepilot.exception.FileProcessingException;
import codepilot.exception.InvalidFileException;
import codepilot.exception.ResourceNotFoundException;
import codepilot.exception.UnsupportedFileTypeException;
import codepilot.repository.DocumentRepository;
import codepilot.repository.ProjectRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Service
public class DocumentService {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            "java", "xml", "properties", "yml", "yaml", "json", "md",
            "py", "js", "ts", "jsx", "tsx", "go", "rb", "php",
            "c", "cpp", "h", "hpp", "cs", "rs", "kt", "sql", "html", "css", "sh"
    );

    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024; // 2 MB per file

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;

    public DocumentService(DocumentRepository documentRepository, ProjectRepository projectRepository) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
    }

    public List<DocumentResponse> uploadFiles(Long projectId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("At least one file must be provided");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        return files.stream()
                .map(file -> saveFile(project, file))
                .map(this::toResponse)
                .toList();
    }

    public List<DocumentResponse> getFilesByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }

        return documentRepository.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Document saveFile(Project project, MultipartFile file) {
        validateFile(file);

        String fileName = file.getOriginalFilename();
        String fileType = extractExtension(fileName);
        String content = readContent(file);

        // V1 simplification: filePath is just the file name.
        Document document = new Document(project, fileName, fileName, fileType, content);
        return documentRepository.save(document);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty: " + file.getOriginalFilename());
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileException("File exceeds maximum size of 2MB: " + file.getOriginalFilename());
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileTypeException(
                    "Unsupported file type '." + extension + "' for file: " + file.getOriginalFilename());
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new UnsupportedFileTypeException("File has no extension: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private String readContent(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileProcessingException("Could not read file: " + file.getOriginalFilename(), e);
        }
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getFilePath(),
                document.getFileType(),
                document.getCreatedAt()
        );
    }
}
