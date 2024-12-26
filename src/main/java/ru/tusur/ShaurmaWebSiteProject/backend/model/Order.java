package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    @JoinColumn(name="user_details_id")
    private UserDetails userDetails;

    @OneToMany(mappedBy = "order")
    private Set<OrderContent> orderContents;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    @ManyToMany
//    @JoinColumn(name="promotion_id")
    private Set<Promotion> promotions = new HashSet<>();

    private BigDecimal sum;

    private BigDecimal deliverySum;

    private String targetAddress;

    private Integer massSum = 0;

    private Date orderCreationDate;

    private Date orderStateDate;

    private boolean featured = false;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
}


