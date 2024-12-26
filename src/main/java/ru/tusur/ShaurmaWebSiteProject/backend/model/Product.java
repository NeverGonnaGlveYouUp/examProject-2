package ru.tusur.ShaurmaWebSiteProject.backend.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private String previewUrl;

    private BigDecimal price;

    private Integer mass;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer rank;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private ProductTypeEntity productType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<Review> reviews;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "productSet", fetch = FetchType.EAGER)
    private Set<ProductOption> productOptions;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<OrderContent> orderContents;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "content_map", joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "content_name")
    @Column(name = "content_mass")
    private Map<String, Integer> contentMap = new HashMap<>();

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Transient
    Integer num = 0;

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
