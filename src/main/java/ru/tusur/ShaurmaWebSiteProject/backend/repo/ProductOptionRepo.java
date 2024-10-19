package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;

import java.util.List;

@Component
public interface ProductOptionRepo extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllProductOptionByProductSet(Product product);
}
