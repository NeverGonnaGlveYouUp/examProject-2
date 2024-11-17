package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;

import java.util.List;

@Repository(value = "customBranchProductRepository")
public interface CustomBranchProductRepository{

    @Query(value = "SELECT COUNT(bp) FROM BranchProduct bp WHERE bp.branch = :branch")
    Long countByBranch(@Param("b_id") Long b_id);

    @Query(value = "SELECT branch_id, product_id, hide\n" +
            "FROM branch_product\n", nativeQuery = true)
    List<BranchProduct> getAll();

    @Query(value = "SELECT branch_id, product_id, hide\n" +
            "FROM branch_product\n" +
            "WHERE branch_id=:b_id AND product_id=:p_id", nativeQuery = true)
    BranchProduct getOne(@Param("b_id") Long b_id, @Param("p_id") Long p_id);

    @Query(value = "INSERT INTO branch_product\n" +
            "(hide, branch_id, product_id)\n" +
            "VALUES(:b, :b_id, :p_id)", nativeQuery = true)
    void create(@Param("b") boolean b, @Param("b_id") Long b_id, @Param("p_id") Long p_id);

    @Query(value = "UPDATE branch_product\n" +
            "SET hide=:b\n" +
            "WHERE branch_id=:b_id AND product_id=:p_id", nativeQuery = true)
    void update(@Param("b") boolean b, @Param("b_id") Long b_id, @Param("p_id") Long p_id);

    @Query(value = "DELETE FROM branch_product\n" +
            "WHERE branch_id=:b_id AND product_id=:p_id", nativeQuery = true)
    void delete(@Param("b_id") Long b_id, @Param("p_id") Long p_id);
}
