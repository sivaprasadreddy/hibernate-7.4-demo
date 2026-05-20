package com.jetbrains.hibernate7.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_id_generator")
    @SequenceGenerator(name = "order_item_id_generator", sequenceName = "order_item_id_seq")
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Column(name = "product_code", nullable = false)
    String productCode;

    @Column(name = "quantity", nullable = false)
    int quantity;

    protected OrderItem() {}

    public OrderItem(String productCode, int quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
