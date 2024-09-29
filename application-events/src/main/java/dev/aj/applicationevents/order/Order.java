package dev.aj.applicationevents.order;

import dev.aj.applicationevents.customer.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private int quantity;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.ALL})
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    private OrderStatus status;

}
