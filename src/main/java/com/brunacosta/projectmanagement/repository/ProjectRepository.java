package com.brunacosta.projectmanagement.repository;

import com.brunacosta.projectmanagement.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    @EntityGraph(attributePaths = "employees")
    List<Project> findAll();

    @Override
    @EntityGraph(attributePaths = "employees")
    Optional<Project> findById(Long id);
}