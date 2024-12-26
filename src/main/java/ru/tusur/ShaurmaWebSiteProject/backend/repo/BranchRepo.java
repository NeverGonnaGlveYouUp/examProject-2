package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;

import java.util.List;
import java.util.Optional;

public interface BranchRepo extends JpaRepository<Branch, Long> {
    List<Branch> findAllByHide(boolean hide);
    Optional<Branch> findAllByAddress(String address);
}
