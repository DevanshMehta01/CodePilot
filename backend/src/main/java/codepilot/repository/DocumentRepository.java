package codepilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import codepilot.entity.Document;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByProjectId(Long projectId);
}
