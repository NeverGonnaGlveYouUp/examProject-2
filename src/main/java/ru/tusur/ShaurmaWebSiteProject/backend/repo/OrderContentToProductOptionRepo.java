package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContentToProductOption;

import java.util.Optional;

public interface OrderContentToProductOptionRepo extends JpaRepository<OrderContentToProductOption, Long> {
    @Transactional
    Optional<OrderContentToProductOption> findByOrderContent(OrderContent orderContent);
}
