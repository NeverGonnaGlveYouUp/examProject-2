package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

}
