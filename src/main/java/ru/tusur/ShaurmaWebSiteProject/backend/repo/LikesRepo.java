package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Likes;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;

import java.util.List;
import java.util.Optional;

public interface LikesRepo extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserDetailsAndReview(UserDetails userDetails, Review review);
    List<Likes> findAllByReview(Review review);
}
