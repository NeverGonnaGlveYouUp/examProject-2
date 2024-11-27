package ru.tusur.ShaurmaWebSiteProject.backend.model;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.mapping.List;

import java.util.Date;
import java.util.Set;


@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Branch {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String phoneNumber;

    private String deliveryZoneUrl;

    private String deliveryStreets;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date openFrom;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date openTill;

    @OneToMany(mappedBy="branch")
    private Set<Review> reviews;
}
