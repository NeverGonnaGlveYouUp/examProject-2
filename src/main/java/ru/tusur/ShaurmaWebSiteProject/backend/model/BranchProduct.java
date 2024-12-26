package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table
public class BranchProduct {
    @EmbeddedId
    BranchProductKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne
    @MapsId("branchId")
    @JoinColumn(name = "branch_id")
    Branch branch;

    boolean hide = false;

    @Override
    public String toString() {
        return "BranchProduct{" +
                "branch=" + branch +
                ", id=" + id +
                ", product=" + product +
                ", hide=" + hide +
                '}';
    }
}
