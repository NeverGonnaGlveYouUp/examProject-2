package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tusur.ShaurmaWebSiteProject.backend.config.SimpleCacheCustomizer;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findByProductTypeOrderByRankAsc(ProductTypeEntity productType);
    Optional<Product> findByName(String name);
    List<Product> findByProductType(ProductTypeEntity productType);
}