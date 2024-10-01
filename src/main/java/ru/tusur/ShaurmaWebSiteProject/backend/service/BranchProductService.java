package ru.tusur.ShaurmaWebSiteProject.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;

import java.util.Collection;
import java.util.Optional;

@Service
public class BranchProductService {

    @Autowired
    BranchProductRepo branchProductRepo;

    public Collection<BranchProduct> findAll() {
        return branchProductRepo.findAll();
    }

    public BranchProduct add(BranchProduct branchProduct) {
        System.out.println("asdasdAdd");
        return branchProductRepo.save(branchProduct);
    }

    public BranchProduct update(BranchProduct branchProduct) {
        System.out.println("asdasdUpdate");
        return branchProductRepo.save(branchProduct);
    }

    public void delete(BranchProduct branchProduct) {
        System.out.println("asdasdDel");
        branchProductRepo.delete(branchProduct);
    }

    public Optional<BranchProduct> findById(Long id) {
        System.out.println("asdasdById");
        return branchProductRepo.findById(id);
    }
}
