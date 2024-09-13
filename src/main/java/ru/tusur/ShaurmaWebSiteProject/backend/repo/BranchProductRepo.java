package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;

public interface BranchProductRepo extends JpaRepository<BranchProduct, Long> {
}
