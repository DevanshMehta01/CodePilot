package codepilot.service;

import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import codepilot.dto.document.DocumentResponse;
import codepilot.entity.Document;
import codepilot.entity.Project;
import codepilot.exception.InvalidFileException;
import codepilot.exception.ResourceNotFoundException;
import codepilot.exception.UnsupportedFileTypeException;
import codepilot.repository.DocumentRepository;
import codepilot.repository.ProjectRepository;
import codepilot.service.DocumentService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void uploadFilesRejectsUnsupportedExtension() {
        Project project = new Project("demo", "desc");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        MultipartFile badFile = new MockMultipartFile(
                "files", "notes.txt", "text/plain", "hello".getBytes());

        assertThatThrownBy(() -> documentService.uploadFiles(1L, List.of(badFile)))
                .isInstanceOf(UnsupportedFileTypeException.class);
    }

    @Test
    void uploadFilesRejectsEmptyFile() {
        Project project = new Project("demo", "desc");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        MultipartFile emptyFile = new MockMultipartFile(
                "files", "Empty.java", "text/plain", new byte[0]);

        assertThatThrownBy(() -> documentService.uploadFiles(1L, List.of(emptyFile)))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    void uploadFilesThrowsWhenProjectMissing() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        MultipartFile file = new MockMultipartFile(
                "files", "Foo.java", "text/plain", "class Foo {}".getBytes());

        assertThatThrownBy(() -> documentService.uploadFiles(99L, List.of(file)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void uploadFilesSavesValidJavaFile() {
        Project project = new Project("demo", "desc");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(documentRepository.save(any(Document.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile file = new MockMultipartFile(
                "files", "Foo.java", "text/plain", "class Foo {}".getBytes());

        List<DocumentResponse> result = documentService.uploadFiles(1L, List.of(file));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("Foo.java");
        assertThat(result.get(0).getFileType()).isEqualTo("java");
    }
}
