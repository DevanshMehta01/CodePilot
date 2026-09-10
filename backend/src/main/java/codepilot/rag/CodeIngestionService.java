package codepilot.rag;

import org.springframework.ai.vectorstore.VectorStore; 
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import codepilot.entity.Document;
import codepilot.exception.IndexingException;
import codepilot.repository.DocumentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CodeIngestionService {

    private final DocumentRepository documentRepository;
    private final CodeChunkService codeChunkService;
    private final VectorStore vectorStore;

    public CodeIngestionService(DocumentRepository documentRepository,
                                 CodeChunkService codeChunkService,
                                 VectorStore vectorStore) {
        this.documentRepository = documentRepository;
        this.codeChunkService = codeChunkService;
        this.vectorStore = vectorStore;
    }

    public IndexResult indexProject(Long projectId) {
        List<Document> documents = documentRepository.findByProjectId(projectId);

        if (documents.isEmpty()) {
            throw new IndexingException(
                    "No files to index for project " + projectId + ". Upload files first.");
        }

        removeExistingVectors(projectId);

        List<org.springframework.ai.document.Document> aiDocuments = buildChunkDocuments(projectId, documents);

        try {
            vectorStore.add(aiDocuments);
        } catch (Exception e) {
            throw new IndexingException(
                    "Failed to store embeddings for project " + projectId + ": " + e.getMessage());
        }

        return new IndexResult(documents.size(), aiDocuments.size());
    }

    private List<org.springframework.ai.document.Document> buildChunkDocuments(Long projectId, List<Document> documents) {
        List<org.springframework.ai.document.Document> aiDocuments = new ArrayList<>();

        for (Document document : documents) {
            List<String> chunks = codeChunkService.chunk(document.getContent());

            for (int i = 0; i < chunks.size(); i++) {
                Map<String, Object> metadata = Map.of(
                        "projectId", projectId,
                        "fileName", document.getFileName(),
                        "filePath", document.getFilePath(),
                        "fileType", document.getFileType(),
                        "chunkNumber", i + 1
                );

                aiDocuments.add(new org.springframework.ai.document.Document(chunks.get(i), metadata));
            }
        }

        return aiDocuments;
    }

    private void removeExistingVectors(Long projectId) {
        var filterExpression = new FilterExpressionBuilder()
                .eq("projectId", projectId)
                .build();
        vectorStore.delete(filterExpression);
    }
}
