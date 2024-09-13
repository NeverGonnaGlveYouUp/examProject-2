package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BranchProductKey  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id")
    Long productId;

    @Column(name = "branch_id")
    Long branchId;

}
