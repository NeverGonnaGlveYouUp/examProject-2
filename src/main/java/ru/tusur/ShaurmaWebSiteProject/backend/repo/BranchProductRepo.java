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
public interface BranchProductRepo extends JpaRepository<BranchProduct, Long> {

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

    @Query(value = "SELECT branch_id, product_id, hide\n" +
            "FROM branch_product1\n", nativeQuery = true)
    List<BranchProduct> getAll();

    @Query(value = "SELECT branch_id, product_id, hide\n" +
            "FROM branch_product1 " +
            "WHERE branch_id=:b_id AND product_id=:p_id", nativeQuery = true)
    BranchProduct getOne(@Param("b_id") Long b_id, @Param("p_id") Long p_id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO branch_product1\n" +
            "(hide, branch_id, product_id)\n" +
            "VALUES(:b, :b_id, :p_id)", nativeQuery = true)
    void create(@Param("b") boolean b, @Param("b_id") Long b_id, @Param("p_id") Long p_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE branch_product1\n" +
            "SET hide=:b\n" +
            "WHERE branch_id=:b_id AND product_id=:p_id", nativeQuery = true)
    void update(@Param("b") boolean b, @Param("b_id") Long b_id, @Param("p_id") Long p_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM branch_product1\n" +
            "WHERE branch_id=:b_id AND product_id=:p_id", nativeQuery = true)
    void delete(@Param("b_id") Long b_id, @Param("p_id") Long p_id);
}
