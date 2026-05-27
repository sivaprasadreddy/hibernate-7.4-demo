package com.jetbrains.hibernate7.demo;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

import java.util.List;

public class HibernateUtils {
    public static final String DB_H2 = "h2";
    public static final String DB_POSTGRES = "postgres";

    static SessionFactory getSessionFactory(String db) {
        List<Class<?>> entityClasses = List.of(
                Product.class,
                Order.class,
                OrderItem.class
        );
        if(db.equals(DB_POSTGRES)) {
            return new HibernatePersistenceConfiguration("Default")
                    .managedClasses(entityClasses)
                    .jdbcPoolSize(16)
                    .showSql(true, true, true)
                    .jdbcDriver("org.testcontainers.jdbc.ContainerDatabaseDriver")
                    .jdbcUrl("jdbc:tc:postgresql:18-alpine:///db?TC_INITSCRIPT=schema.sql")
                    .createEntityManagerFactory();
        } else {
            return new HibernatePersistenceConfiguration("Default")
                    .managedClasses(entityClasses)
                    .jdbcPoolSize(16)
                    .showSql(true, true, true)
                    .jdbcUrl("jdbc:h2:mem:db1")
                    .jdbcCredentials("sa", "")
                    .createEntityManagerFactory();
        }
    }
}