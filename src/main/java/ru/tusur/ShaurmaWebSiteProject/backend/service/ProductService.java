package ru.tusur.ShaurmaWebSiteProject.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.crudui.crud.CrudListener;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements CrudListener<Product> {

    private ProductRepo productRepo;

    @Override
    public Collection<Product> findAll() {
        return productRepo.findAll();
    }

    @Override
    public Product add(Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product update(Product product) {
        return productRepo.save(product);
    }

    @Override
    public void delete(Product product) {
        productRepo.delete(product);
    }
}
