package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
public class ProductTypeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy="productType")
    private Set<Product> products;
}
