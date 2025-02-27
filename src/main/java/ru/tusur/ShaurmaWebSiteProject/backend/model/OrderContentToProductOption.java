package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class OrderContentToProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_content_id", referencedColumnName = "order_id")
    @JoinColumn(name = "order_content_product_id", referencedColumnName = "product_id")
    private OrderContent orderContent;

//    @OneToMany
    @ManyToMany
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "product_option_id"))
    private Set<ProductOption> productOptionSet;

}
