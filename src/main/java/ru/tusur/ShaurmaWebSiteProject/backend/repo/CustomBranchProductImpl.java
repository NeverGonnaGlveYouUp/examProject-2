package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;

import java.util.ArrayList;
import java.util.List;

public class CustomBranchProductImpl implements CustomBranchProductRepo{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long countByBranch(Branch branch){
        String jpql = "SELECT COUNT(bp) FROM BranchProduct bp WHERE bp.branch = :branch";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("branch", branch.getId());
        return query.getSingleResult();
    }

//    public List<BranchProduct> findAllasdByBranch(Branch branch, ProductTypeEntity productType) {
//        int pageNumber = 1;
//        int pageSize = 4;
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//
//        CriteriaQuery<BranchProduct> branchProductCriteriaQuery = criteriaBuilder.createQuery(BranchProduct.class);
//        Root<BranchProduct> fromBranchProducts = branchProductCriteriaQuery.from(BranchProduct.class);
//        Join<BranchProduct, Product> products = fromBranchProducts.join("products");
//        List<Predicate> conditions = new ArrayList<>();
//        conditions.add(criteriaBuilder.equal(fromBranchProducts.get("branch"), branch));
//        conditions.add(criteriaBuilder.isFalse(fromBranchProducts.get("hide")));
//        conditions.add(criteriaBuilder.equal(products.get("productTypeEntity"), productType));
//
//        TypedQuery<BranchProduct> typedQuery = entityManager.createQuery(branchProductCriteriaQuery
//                .select(fromBranchProducts)
//                .where(conditions.toArray(new Predicate[] {}))
//                .orderBy(criteriaBuilder.asc(fromBranchProducts.get("rack")))
//                .distinct(true));
//
//        return typedQuery.getResultList();
//    }
}
