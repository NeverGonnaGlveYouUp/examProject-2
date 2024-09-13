package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table
public class OrderContent {

    @EmbeddedId
    OrderContentKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "id", nullable = false)
    private Branch branch;

//    @OneToOne
//    @JoinColumn(name = "order_content_product_option_id", referencedColumnName = "id")
//    private OrderContentProductOption orderContentProductOption;

    @OneToMany
    private Set<ProductOption> productOptionSet;

    int num;

}
