package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Order;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;

import java.util.List;

public interface OrderContentRepo extends JpaRepository<OrderContent, Long> {
    List<OrderContent> findAllByOrder(Order order);
}
