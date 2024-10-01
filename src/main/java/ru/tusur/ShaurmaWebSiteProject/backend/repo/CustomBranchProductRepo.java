package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;

import java.util.List;

public interface CustomBranchProductRepo {
    Long countByBranch(Branch branch);

//    List<BranchProduct> findAllasdByBranch(Branch branch, ProductTypeEntity productType);
}
