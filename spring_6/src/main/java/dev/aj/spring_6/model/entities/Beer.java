package dev.aj.spring_6.model.entities;

import dev.aj.spring_6.model.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Beer {

    @Id
    @UuidGenerator
    @Column(length = 36, columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 20, nullable = false)
    private String beerName;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(length = 20, nullable = false, columnDefinition = "varchar(30) default IPA")
    private BeerStyle beerStyle;

    @NotBlank
    private String upc;

    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;

    @Column(columnDefinition = "VARCHAR(30)", length = 30)
    private String batch;

    @Builder.Default
    // This ensures that default when creating a Beer object will have 'new Hashset<>()' rather than 'null'
    @ManyToMany
    @JoinTable(
            name = "beer_category",
            joinColumns = @JoinColumn(name = "beer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<Category> categories = new HashSet<>();


    @Version
    private Integer version;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdTime;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedTime;

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getBeers().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBeers().remove(this);
    }
}
