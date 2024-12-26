package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Promotion;
import ru.tusur.ShaurmaWebSiteProject.backend.model.PromotionType;

import java.util.List;
import java.util.Optional;

public interface PromotionRepo extends JpaRepository<Promotion, Long> {
    List<Promotion> findByPromotionType(PromotionType promotionType);
    List<Promotion> findByConditionAndHide(String condition, boolean b);
}
