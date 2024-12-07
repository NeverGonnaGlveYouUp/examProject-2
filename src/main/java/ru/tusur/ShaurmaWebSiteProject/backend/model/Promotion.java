package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@ToString
@Setter
@Table
public class Promotion{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String promotionPreviewUrl;

    private String promotionMainUrl;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;

    @NotNull
    private String condition;

    private BigDecimal promotionEffect;

//    private Product product;

    @OneToMany(mappedBy = "promotion")
    private Set<Order> orders;
}
