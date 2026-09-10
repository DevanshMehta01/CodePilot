package codepilot.service;

import org.springframework.stereotype.Service;

import codepilot.dto.chat.ChatResponse;
import codepilot.exception.ResourceNotFoundException;
import codepilot.rag.RagResult;
import codepilot.rag.RagService;
import codepilot.repository.ProjectRepository;

@Service
public class ChatService {

    private final ProjectRepository projectRepository;
    private final RagService ragService;

    public ChatService(ProjectRepository projectRepository, RagService ragService) {
        this.projectRepository = projectRepository;
        this.ragService = ragService;
    }

    public ChatResponse askQuestion(Long projectId, String question) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }

        RagResult result = ragService.answer(projectId, question);
        return new ChatResponse(result.answer(), result.sources());
    }
}
