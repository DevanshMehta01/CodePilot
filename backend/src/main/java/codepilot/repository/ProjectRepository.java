package codepilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import codepilot.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // JpaRepository already provides save(), findById(), findAll(), existsById(), etc.
}
