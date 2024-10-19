package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProductKey;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchProductRepo extends JpaRepository<BranchProduct, Long>, CustomBranchProductRepo {

    Optional<BranchProduct> findTopByOrderByIdDesc();

    Optional<BranchProduct> findTopByBranchAddress(String address);

    BranchProduct findById(BranchProductKey branchProductKey);

    List<Product> findAllProductByBranch(Branch branch);
    List<BranchProduct> findAllByBranch(Branch branch);

    @Modifying
    @Transactional
    @Query("update BranchProduct bp set bp.hide= :hide WHERE bp.id= :id")
    void findByIdThenSetHide(@Param("id") BranchProductKey branchProductKey,
                             @Param("hide") boolean b);
}
