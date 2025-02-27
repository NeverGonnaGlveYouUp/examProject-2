package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class ProductContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer mass;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

}
