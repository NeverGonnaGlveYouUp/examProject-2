package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;

import java.util.Optional;

public interface ProductTypeEntityRepo extends JpaRepository<ProductTypeEntity, Long> {
    Optional<ProductTypeEntity> findByName(String name);

    boolean existsProductTypeEntityByName(String name);
}
