package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Order;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
