package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;

import java.util.Optional;

public interface BranchRepo extends JpaRepository<Branch, Long> {
}
