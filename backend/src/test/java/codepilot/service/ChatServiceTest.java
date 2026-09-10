package codepilot.service;

import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import codepilot.dto.chat.ChatResponse;
import codepilot.dto.chat.SourceReference;
import codepilot.exception.ResourceNotFoundException;
import codepilot.rag.RagResult;
import codepilot.rag.RagService;
import codepilot.repository.ProjectRepository;
import codepilot.service.ChatService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RagService ragService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void askQuestionThrowsWhenProjectMissing() {
        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> chatService.askQuestion(99L, "How does X work?"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void askQuestionDelegatesToRagServiceAndMapsResult() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        RagResult ragResult = new RagResult(
                "Product creation starts in ProductController...",
                List.of(new SourceReference("ProductController.java", "ProductController.java"))
        );
        when(ragService.answer(1L, "How does product creation work?")).thenReturn(ragResult);

        ChatResponse response = chatService.askQuestion(1L, "How does product creation work?");

        assertThat(response.getAnswer()).contains("ProductController");
        assertThat(response.getSources()).hasSize(1);
        assertThat(response.getSources().get(0).getFileName()).isEqualTo("ProductController.java");
    }
}
