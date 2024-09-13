package ru.tusur.ShaurmaWebSiteProject.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.vaadin.crudui.crud.CrudListener;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Component
public class ProductService implements CrudListener<Product> {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductTypeEntityRepo productTypeEntityRepo;

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

    @CacheEvict(value = "products", allEntries = true)
    public List<Product> cacheAllProducts(){
        List<Product> products = new LinkedList<>();
        this.productTypeEntityRepo.findAll().forEach(productType -> products.addAll(productRepo.findByProductType(productType)));
        return products;
    }
}
