package com.jetbrains.hibernate7.demo;

import jakarta.persistence.*;
import org.hibernate.annotations.Audited;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Audited
@Audited.Table(name="orders_aud_log",
        changesetIdColumn="REV",
        modificationTypeColumn="REVTYPE")
class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_generator")
    @SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_seq")
    Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    UUID orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    Set<OrderItem> items = new HashSet<>();

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber=" + orderNumber +
                ", items=" + items +
                ", status=" + status +
                '}';
    }
}
