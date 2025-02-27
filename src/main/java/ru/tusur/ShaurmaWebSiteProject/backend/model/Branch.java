package ru.tusur.ShaurmaWebSiteProject.backend.model;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.List;

import java.util.Date;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;
        return id.equals(branch.id) && Objects.equals(address, branch.address) && Objects.equals(phoneNumber, branch.phoneNumber)  && Objects.equals(deliveryStreets, branch.deliveryStreets) && Objects.equals(openFrom, branch.openFrom) && Objects.equals(openTill, branch.openTill);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + Objects.hashCode(address);
        result = 31 * result + Objects.hashCode(phoneNumber);
        result = 31 * result + Objects.hashCode(deliveryStreets);
        result = 31 * result + Objects.hashCode(openFrom);
        result = 31 * result + Objects.hashCode(openTill);
        return result;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "address='" + address + '\'' +
                ", id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", deliveryStreets='" + deliveryStreets + '\'' +
                ", openFrom=" + openFrom +
                ", openTill=" + openTill +
                '}';
    }

    private String address;

    private String phoneNumber;


    private String deliveryStreets;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date openFrom;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date openTill;

    @OneToMany(mappedBy="branch")
    private Set<Review> reviews;

    boolean hide = false;
}
