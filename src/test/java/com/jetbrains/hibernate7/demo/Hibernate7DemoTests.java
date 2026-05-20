package com.jetbrains.hibernate7.demo;

import jakarta.persistence.Tuple;
import org.hibernate.SessionFactory;
import org.hibernate.audit.AuditLogFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Hibernate7DemoTests {

    static SessionFactory sessionFactory;

    @BeforeAll
    static void init() {
        sessionFactory = HibernateUtils.getSessionFactory(HibernateUtils.DB_H2);
    }

    @Test
    void loadOrdersPageWithChildCollection() {
        sessionFactory.inSession(session -> {
            List<Order> orders =
                    session.createSelectionQuery("from Order o join fetch o.items", Order.class)
                            .setMaxResults(5)
                            .getResultList();
            orders.forEach(out::println);
        });
    }

    @Test
    void updateProductPrice() {
        Instant initialTime;
        Long productId;
        try (var session = sessionFactory.withOptions().open()) {
            session.beginTransaction();
            Product newProduct = new Product();
            String random = UUID.randomUUID().toString();
            newProduct.code = "P"+random;
            newProduct.name = "Product-"+random;
            newProduct.price = BigDecimal.valueOf(90.00);
            newProduct.description = "Product Description "+random;
            session.persist(newProduct);
            session.getTransaction().commit();

            productId = newProduct.id;
            initialTime = Instant.now();

            session.beginTransaction();
            var product =  session.find(Product.class, newProduct.id);
            product.price = BigDecimal.valueOf(100.00);
            session.merge(product);
            session.getTransaction().commit();
        }

        try (var session = sessionFactory.withOptions().asOf(initialTime).open()) {
            var product =  session.find(Product.class, productId);
            assertEquals(0, new BigDecimal("90.0").compareTo(product.price));
        }
    }

    @Test
    void updateOrderAndVerifyAuditLog() {
        try (var session = sessionFactory.withOptions().open()) {
            session.beginTransaction();
            Order newOrder = new Order();
            newOrder.orderNumber = UUID.randomUUID();
            newOrder.status = OrderStatus.NEW;
            session.persist(newOrder);
            session.getTransaction().commit();

            session.beginTransaction();
            var order =  session.find(Order.class, newOrder.id);
            order.status = OrderStatus.IN_PROCESS;
            session.merge(order);
            session.getTransaction().commit();

            List<Tuple> logEntries = session.createNativeQuery("select * from orders_aud_log where id = :id", Tuple.class)
                    .setParameter("id", newOrder.id)
                    .getResultList();
            assertEquals(2, logEntries.size());
            List<String> statusList = logEntries.stream().map(t -> t.get("status", String.class))
                    .toList();
            assertTrue(statusList.containsAll(List.of("NEW", "IN_PROCESS")));

            final var auditLog = AuditLogFactory.create( session );
            final var revisionIds = auditLog.getChangesets( Order.class, newOrder.id );
            assertEquals( 2, revisionIds.size() );

            /*final var revisions = session.createSelectionQuery(
                    "from DefaultChangelog where id in :ids order by id",
                    DefaultChangelog.class
            ).setParameter( "ids", revisionIds ).getResultList();
            assertEquals( 2, revisions.size() );*/
        }
    }
}
