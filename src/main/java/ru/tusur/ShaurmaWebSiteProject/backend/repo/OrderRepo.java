package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Order;

public interface OrderRepo extends JpaRepository<Order, Long> { }
