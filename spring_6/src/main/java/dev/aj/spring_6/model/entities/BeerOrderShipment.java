package dev.aj.spring_6.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "beer_order_shipment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BeerOrderShipment {

    @Id
    @UuidGenerator
    @Column(length = 36, columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String trackingNumber;

    @OneToOne
    @JoinColumn(name = "beer_order_id", referencedColumnName = "id")
    private BeerOrder beerOrder;

    @Version
    private Integer version;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdTime;

    @LastModifiedDate
    @Column(insertable = false)
    private Instant updatedTime;

}
