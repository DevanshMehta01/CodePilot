package codepilot.dto.project;

public class IndexResponse {

    private final Long projectId;
    private final int filesProcessed;
    private final int chunksIndexed;

    public IndexResponse(Long projectId, int filesProcessed, int chunksIndexed) {
        this.projectId = projectId;
        this.filesProcessed = filesProcessed;
        this.chunksIndexed = chunksIndexed;
    }

    public Long getProjectId() {
        return projectId;
    }

    public int getFilesProcessed() {
        return filesProcessed;
    }

    public int getChunksIndexed() {
        return chunksIndexed;
    }
}
