package ru.tusur.ShaurmaWebSiteProject.backend.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Review {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer grade;

    @ManyToOne
    @JoinColumn(name="user_details_id", nullable=false)
    private UserDetails userDetails;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="branch_id", nullable=false)
    private Branch branch;

    @OneToMany(mappedBy="review")
    private Set<Likes> likes;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date date;
}
