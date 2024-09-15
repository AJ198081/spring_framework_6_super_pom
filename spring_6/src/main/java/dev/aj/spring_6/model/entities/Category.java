package dev.aj.spring_6.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
public class Category {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(length = 36, columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String description;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "beer_category",
            joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id", referencedColumnName = "id")
    )
    private Set<Beer> beers = new HashSet<>();

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdTime;

    @UpdateTimestamp
    @Column(insertable = false)
    private Instant updatedTime;
}
