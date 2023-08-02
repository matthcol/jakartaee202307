package org.example.bootstrap;

import jakarta.persistence.EntityManager;
import org.example.entity.Movie;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class JpaBootstrap {

    protected JpaBootstrap() {
    }

    public static SessionFactory createSessionFactory() {
        ServiceRegistry standardRegistry =
                new StandardServiceRegistryBuilder()
                        .applySettings(getProperties())
                        .build();
        Metadata metadata = new MetadataSources(standardRegistry)
                // Persistent classes
                .addAnnotatedClass(Movie.class)
                .getMetadataBuilder()
                .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
                .build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
                .build();
        return sessionFactory;
    }


    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.url", "jdbc:h2:mem:dbmovie;NON_KEYWORDS=year");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.pool_size", 1);
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("", "");
        return properties;
    }

    public static EntityManager createEntityManager() {
        return createSessionFactory().createEntityManager();
    }

}
