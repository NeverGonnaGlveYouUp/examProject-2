package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name="user_details_id")
    private UserDetails userDetails;

    @OneToMany(mappedBy = "order")
    private Set<OrderContent> orderContents;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name="promotion_id")
    private Promotion promotion;

    private BigDecimal sum;

    private String targetAddress;

    private Integer massSum;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;
}


