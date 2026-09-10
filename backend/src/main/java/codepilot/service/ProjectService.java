package codepilot.service;

import org.springframework.stereotype.Service;

import codepilot.dto.project.IndexResponse;
import codepilot.dto.project.ProjectRequest;
import codepilot.dto.project.ProjectResponse;
import codepilot.entity.Project;
import codepilot.exception.ResourceNotFoundException;
import codepilot.rag.CodeIngestionService;
import codepilot.rag.IndexResult;
import codepilot.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CodeIngestionService codeIngestionService;

    public ProjectService(ProjectRepository projectRepository, CodeIngestionService codeIngestionService) {
        this.projectRepository = projectRepository;
        this.codeIngestionService = codeIngestionService;
    }

    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project(request.getName(), request.getDescription());
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = findProjectOrThrow(id);
        return toResponse(project);
    }

    public IndexResponse indexProject(Long id) {
        findProjectOrThrow(id); // fail fast with 404 if project doesn't exist
        IndexResult result = codeIngestionService.indexProject(id);
        return new IndexResponse(id, result.documentsProcessed(), result.chunksIndexed());
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }
}
