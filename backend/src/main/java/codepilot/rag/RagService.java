package codepilot.rag;

import org.springframework.ai.chat.client.ChatClient; 
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import codepilot.dto.chat.SourceReference;
import codepilot.exception.LlmException;
import codepilot.exception.VectorSearchException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final int TOP_K = 5;

    private static final String SYSTEM_PROMPT = """
            You are CodePilot, an AI assistant that explains software codebases to developers.
            Answer the user's question using only the provided repository context below.
            Do not invent classes, methods, files, APIs, or behavior that is not present in the provided context.
            If the provided context is insufficient to answer confidently, clearly say there is not enough information.
            When possible, explain the execution flow in simple, step-by-step terms.
            Mention the relevant source files by name when explaining your answer.
            You are working with a small set of retrieved code snippets, not the entire codebase --
            never claim to have read files that were not provided in the context.
            
            Do not use Markdown headings (###).
Use plain text section titles instead.
Avoid unnecessary Markdown formatting.
            """;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public RagResult answer(Long projectId, String question) {
        List<Document> retrievedChunks = retrieveRelevantChunks(projectId, question);

        if (retrievedChunks.isEmpty()) {
            return new RagResult(
                    "I couldn't find any indexed content related to this question. "
                            + "Make sure the project has been indexed and try rephrasing your question.",
                    List.of()
            );
        }

        String answer = generateAnswer(question, retrievedChunks);
        List<SourceReference> sources = extractSources(retrievedChunks);

        return new RagResult(answer, sources);
    }

    private List<Document> retrieveRelevantChunks(Long projectId, String question) {
        try {
            var filterExpression = new FilterExpressionBuilder()
                    .eq("projectId", projectId)
                    .build();

            SearchRequest searchRequest = SearchRequest.builder()
                    .query(question)
                    .topK(TOP_K)
                    .filterExpression(filterExpression)
                    .build();

            return vectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            throw new VectorSearchException("Failed to search indexed code: " + e.getMessage());
        }
    }

    private String generateAnswer(String question, List<Document> retrievedChunks) {
        String context = buildContext(retrievedChunks);
        String userPrompt = """
                Repository context:
                %s

                Question: %s

                Answer the question using only the repository context above.
                """.formatted(context, question);

        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            throw new LlmException("Failed to generate an answer: " + e.getMessage());
        }
    }

    private String buildContext(List<Document> chunks) {
        return chunks.stream()
                .map(chunk -> "File: " + chunk.getMetadata().get("fileName")
                        + "\n---\n"
                        + chunk.getText())
                .collect(Collectors.joining("\n\n"));
    }

    private List<SourceReference> extractSources(List<Document> chunks) {
        Set<SourceReference> unique = new LinkedHashSet<>();
        for (Document chunk : chunks) {
            String fileName = String.valueOf(chunk.getMetadata().get("fileName"));
            String filePath = String.valueOf(chunk.getMetadata().get("filePath"));
            unique.add(new SourceReference(fileName, filePath));
        }
        return List.copyOf(unique);
    }
}
