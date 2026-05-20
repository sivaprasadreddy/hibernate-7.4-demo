# Hibernate 7.4 Demo
A sample repository to explore new features in Hibernate 7.4.

The project is configured with H2 and Postgres(using Testcontainers).

* Run tests in `Hibernate7DemoTests` which uses H2 by default.
* To use Postgres database, change `HibernateUtils.DB_H2` to `HibernateUtils.DB_POSTGRES` in `Hibernate7DemoTests.init()` method.
