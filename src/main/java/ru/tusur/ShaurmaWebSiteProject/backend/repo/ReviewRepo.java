package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;

import java.util.Date;
import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findAllByProduct(Product product);
    List<Review> findAllByProductAndBranch(Product product, Branch branch);
    List<Review> findAllByDateBetween(Date start, Date end);
}
