package org.kesler.cartreg;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.kesler.cartreg.dao.CartSetChangeDAO;
import org.kesler.cartreg.dao.CartSetDAO;
import org.kesler.cartreg.dao.CartTypeDAO;
import org.kesler.cartreg.dao.PlaceDAO;
import org.kesler.cartreg.dao.support.CartSetChangeDAOImpl;
import org.kesler.cartreg.dao.support.CartSetDAOImpl;
import org.kesler.cartreg.dao.support.CartTypeDAOImpl;
import org.kesler.cartreg.dao.support.PlaceDAOImpl;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.CartTypeService;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.service.support.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by alex on 29.11.14.
 */
@Configuration
//@ComponentScan({"org.kesler.*"})
//@EnableTransactionManagement
//@Import(CartRegAppRepositoryFactory.class)
public class CartRegAppServiceFactory {


    @Bean
    public PlaceService placeService() {
        return new PlaceServiceDAOImpl();
    }

    @Bean
    public CartTypeService cartTypeService() {
        return new CartTypeServiceDAOImpl();
    }

    @Bean
    public CartSetService cartSetService() {
        return new CartSetServiceDAOImpl();
    }

    @Bean
    public CartSetChangeService cartSetChangeService() {
        return new CartSetChangeServiceDAOImpl();
    }


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://10.10.0.170:3306/cartreg");
        ds.setUsername("croper");
        ds.setPassword("Qwerty123");

        return ds;
    }


    private Properties getHibernateProperties() {
        Properties prop = new Properties();
//        prop.put("hibernate.connection.driver_class","com.mysql.jdbc.Driver");
//        prop.put("hibernate.connection.url","jdbc:mysql://10.10.0.170:3306/cartreg");
//        prop.put("hibernate.connection.username","croper");
//        prop.put("hibernate.connection.password","Qwerty123");
//        prop.put("hibernate.connection.pool_size","1");
//        prop.put("hibernate.c3p0.min_size", "5");
//        prop.put("hibernate.c3p0.max_size", "20");
        prop.put("hibernate.format_sql", "true");
        prop.put("hibernate.show_sql", "true");
        prop.put("hibernate.hbm2ddl.auto","update");
        prop.put("hibernate.dialect",
                "org.hibernate.dialect.MySQL5Dialect");

        return prop;
    }


//    public @Bean
//    SessionFactory sessionFactory () {
//        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
//        configuration.setProperties(getHibernateProperties())
//                .addAnnotatedClass(Place.class)
//                .addAnnotatedClass(CartType.class)
//                .addAnnotatedClass(CartSet.class)
//                .addAnnotatedClass(CartSetChange.class)
//                .setProperties(getHibernateProperties());
//
//        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
//                .applySettings(configuration.getProperties()).build();
//
//
//        return configuration
//                .buildSessionFactory(serviceRegistry);
//
//    }


    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());

        builder.scanPackages("org.kesler.cartreg.domain","org.kesler.cartreg.dao");
//        builder.addPackages("org.kesler.cartreg.domain","org.kesler.cartreg.dao");
        builder.setProperties(getHibernateProperties());

        return builder.buildSessionFactory();
    }


    @Bean
    public PlaceDAO placeDAO() {
        return new PlaceDAOImpl();
    }

    @Bean
    public CartTypeDAO cartTypeDAO() {
        return new CartTypeDAOImpl();
    }

    @Bean
    public CartSetDAO cartSetDAO() {
        return new CartSetDAOImpl();
    }

    @Bean
    public CartSetChangeDAO cartSetChangeDAO() {
        return new CartSetChangeDAOImpl();
    }

//    @Bean
//    public PlatformTransactionManager txManager() {
//        return new HibernateTransactionManager(sessionFactory());
//    }


}
