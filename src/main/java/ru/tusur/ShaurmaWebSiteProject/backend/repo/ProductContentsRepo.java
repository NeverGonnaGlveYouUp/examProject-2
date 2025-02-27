package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductContent;

import java.util.List;

public interface ProductContentsRepo extends JpaRepository<ProductContent, Long> {
    List<ProductContent> findAllByProduct(Product product);
}
