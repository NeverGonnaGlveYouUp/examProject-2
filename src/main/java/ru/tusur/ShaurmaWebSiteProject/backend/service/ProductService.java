package ru.tusur.ShaurmaWebSiteProject.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.config.SimpleCacheCustomizer;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private BranchProductRepo branchProductRepo;

    @Cacheable(value = SimpleCacheCustomizer.PRODUCT, key = "#product.id")
    public Product add(Product product) {
        return productRepo.save(product);
    }

    @Caching(evict = {
            @CacheEvict(value = SimpleCacheCustomizer.PRODUCT, key = "#product.id"),
            @CacheEvict(value = SimpleCacheCustomizer.PRODUCTS, allEntries = true)
    })
    public Product update(Product product) {
        return productRepo.save(product);
    }

    @Caching(evict = {
            @CacheEvict(value = SimpleCacheCustomizer.PRODUCT, key = "#product.id"),
            @CacheEvict(value = SimpleCacheCustomizer.PRODUCTS, allEntries = true)
    })
    public void delete(Product product) {
        productRepo.delete(product);
    }

    @Cacheable(value = SimpleCacheCustomizer.PRODUCTS, key = "#productType.id")
    public List<Product> findByProductTypeOrderByRankAsc(ProductTypeEntity productType) {
        return productRepo.findByProductTypeOrderByRankAsc(productType);
    }

    @Cacheable(value = SimpleCacheCustomizer.PRODUCT, key = "#id")
    public Optional<Product> findById(Long id) {
        return productRepo.findById(id);
    }
}
