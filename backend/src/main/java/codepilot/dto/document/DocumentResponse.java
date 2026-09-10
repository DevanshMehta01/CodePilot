package codepilot.dto.document;

import java.time.LocalDateTime;

public class DocumentResponse {

    private final Long id;
    private final String fileName;
    private final String filePath;
    private final String fileType;
    private final LocalDateTime createdAt;

    public DocumentResponse(Long id, String fileName, String filePath, String fileType, LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
