package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findAllByProduct(Product product);
}
