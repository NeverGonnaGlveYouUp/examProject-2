package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;

public interface ProductOptionRepo extends JpaRepository<ProductOption, Long> { }
