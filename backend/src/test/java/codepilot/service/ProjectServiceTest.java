package codepilot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import codepilot.dto.project.IndexResponse;
import codepilot.dto.project.ProjectRequest;
import codepilot.dto.project.ProjectResponse;
import codepilot.entity.Project;
import codepilot.exception.ResourceNotFoundException;
import codepilot.rag.CodeIngestionService;
import codepilot.rag.IndexResult;
import codepilot.repository.ProjectRepository;
import codepilot.service.ProjectService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CodeIngestionService codeIngestionService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProjectSavesAndReturnsResponse() {
        ProjectRequest request = new ProjectRequest();
        request.setName("inventory-project");
        request.setDescription("Sample project");

        Project saved = new Project("inventory-project", "Sample project");
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ProjectResponse response = projectService.createProject(request);

        assertThat(response.getName()).isEqualTo("inventory-project");
        assertThat(response.getDescription()).isEqualTo("Sample project");
    }

    @Test
    void getProjectByIdThrowsWhenNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void indexProjectDelegatesToCodeIngestionService() {
        Project project = new Project("inventory-project", "desc");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(codeIngestionService.indexProject(1L)).thenReturn(new IndexResult(2, 5));

        IndexResponse response = projectService.indexProject(1L);

        assertThat(response.getFilesProcessed()).isEqualTo(2);
        assertThat(response.getChunksIndexed()).isEqualTo(5);
        verify(codeIngestionService).indexProject(1L);
    }

    @Test
    void indexProjectThrowsWhenProjectMissing() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.indexProject(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
