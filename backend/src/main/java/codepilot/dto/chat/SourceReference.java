package codepilot.dto.chat;

import java.util.Objects;

public class SourceReference {

    private final String fileName;
    private final String filePath;

    public SourceReference(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceReference that)) return false;
        return Objects.equals(fileName, that.fileName) && Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, filePath);
    }
}
