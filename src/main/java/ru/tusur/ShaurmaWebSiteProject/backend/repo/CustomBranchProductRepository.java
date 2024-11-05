package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Repository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;

import java.util.List;

@Repository
public interface CustomBranchProductRepository {
    Long countByBranch(Long b_id);
    List<BranchProduct> getAll();
    BranchProduct getOne(Long b_id, Long p_id);
    void create(boolean b, Long b_id, Long p_id);
    void update(boolean b, Long b_id, Long p_id);
    void delete(Long b_id, Long p_id);
}
