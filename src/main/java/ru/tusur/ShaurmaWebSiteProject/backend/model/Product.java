package ru.tusur.ShaurmaWebSiteProject.backend.model;


import com.vaadin.hilla.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.atmosphere.config.service.Get;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@ToString
@Setter
@Table
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    @NotNull
    private String previewUrl;

    private BigDecimal price;

    private Integer mass;

    @Column(columnDefinition="BOOLEAN DEFAULT true")
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer rank;

    @ManyToOne
    @JoinColumn(name="type_id", nullable=false)
    private ProductTypeEntity productType;

    @OneToMany(mappedBy="product")
    private Set<Review> reviews;

    @OneToMany(mappedBy = "product")
    Set<OrderContent> orderContents;

//    @OneToMany(mappedBy = "products")
//    Set<FeaturedOrder> featuredOrders;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name="content_map", joinColumns=@JoinColumn(name="product_id", referencedColumnName = "id"))
    @MapKeyColumn (name="content_name")
    @Column(name="content_mass")
    private Map<String, Integer> contentMap = new HashMap<>();

    public boolean equalsAllParams(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return active == product.active && id.equals(product.id) && Objects.equals(name, product.name) && Objects.equals(previewUrl, product.previewUrl) && Objects.equals(price, product.price) && Objects.equals(mass, product.mass) && Objects.equals(description, product.description) && Objects.equals(rank, product.rank) && productType == product.productType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
