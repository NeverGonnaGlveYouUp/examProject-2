package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Order;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderState;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;

import java.util.Date;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findAllByUserDetailsAndOrderState(UserDetails userDetails, OrderState orderState);
    List<Order> findAllByOrderStateDateBetween(Date start, Date end);
}
