package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;

import java.util.List;

@Component
public class CustomBranchProductImpl implements CustomBranchProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ProductRepo productRepo;
    @Autowired
    BranchRepo branchRepo;

    @Override
    public Long countByBranch(Long b_id) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(bp) FROM BranchProduct bp WHERE bp.branch = :branch", Long.class);
        query.setParameter("branch", b_id);
        return query.getSingleResult();
    }

    @Override
    public List<BranchProduct> getAll() {
        String jpql = "SELECT bp FROM BranchProduct bp";
        TypedQuery<BranchProduct> query = entityManager.createQuery(jpql, BranchProduct.class);
        return query.getResultList();
    }

    @Override
    public BranchProduct getOne(Long b_id, Long p_id) {
        TypedQuery<BranchProduct> query = entityManager.createQuery("SELECT bp FROM BranchProduct bp WHERE bp.branch = :branch_id AND bp.product = :product_id", BranchProduct.class);
        query.setParameter("branch_id", branchRepo.findById(b_id).orElseThrow());
        query.setParameter("product_id", productRepo.findById(p_id).orElseThrow());
        return query.getSingleResult();
    }

    @Override
    public void create(boolean b, Long b_id, Long p_id) {
        Query query = entityManager.createQuery("INSERT INTO BranchProduct bp (hide, branch_id, product_id) VALUES(:hide, :branch_id, :product_id)");
        query.setParameter("hide", b);
        query.setParameter("branch_id", branchRepo.findById(b_id).orElseThrow());
        query.setParameter("product_id", productRepo.findById(p_id).orElseThrow());
        query.executeUpdate();
    }

    @Override
    public void update(boolean b, Long b_id, Long p_id) {
        Query query = entityManager.createQuery("UPDATE BranchProduct bp SET bp.hide= :hide WHERE bp.branch=:branch_id AND bp.product=:product_id");
        query.setParameter("hide", b);
        query.setParameter("branch_id", branchRepo.findById(b_id).orElseThrow());
        query.setParameter("product_id", productRepo.findById(p_id).orElseThrow());
        query.executeUpdate();
    }

    @Override
    public void delete(Long b_id, Long p_id) {
        Query query = entityManager.createQuery("DELETE FROM BranchProduct bp WHERE bp.branch=:branch_id AND bp.product=:product_id");
        query.setParameter("branch_id", branchRepo.findById(b_id).orElseThrow());
        query.setParameter("product_id", productRepo.findById(p_id).orElseThrow());
        query.executeUpdate();
    }


}
