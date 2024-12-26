package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;
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

    @OneToOne
    private OrderContentToProductOption orderContentToProductOption;

    int num = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderContent that = (OrderContent) o;
        return Objects.equals(id, that.id) && product.equals(that.product) && Objects.equals(order, that.order) && Objects.equals(branch, that.branch) && Objects.equals(orderContentToProductOption, that.orderContentToProductOption);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + product.hashCode();
        result = 31 * result + Objects.hashCode(order);
        result = 31 * result + Objects.hashCode(branch);
        result = 31 * result + Objects.hashCode(orderContentToProductOption);
        return result;
    }
}
