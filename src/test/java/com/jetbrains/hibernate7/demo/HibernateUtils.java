package com.jetbrains.hibernate7.demo;

import org.hibernate.SessionFactory;
import org.hibernate.audit.DefaultChangelog;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

import java.util.List;

public class HibernateUtils {

    static SessionFactory getSessionFactory() {
        List<Class<?>> entityClasses = List.of(
                Product.class,
                Order.class,
                OrderItem.class
                //,DefaultChangelog.class
        );
         return new HibernatePersistenceConfiguration("Default")
                        .managedClasses(entityClasses)
                        //.jdbcUrl("jdbc:h2:mem:db1")
                        //.jdbcCredentials("sa", "")
                        .jdbcDriver("org.testcontainers.jdbc.ContainerDatabaseDriver")
                        .jdbcUrl("jdbc:tc:postgresql:18-alpine:///db?TC_INITSCRIPT=schema.sql")
                        .jdbcPoolSize(16)
                        .showSql(true, true, true)
                        .createEntityManagerFactory();
    }
}