package org.kesler.cartreg;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;
import java.util.Properties;


@Configuration
//@ComponentScan({"org.kesler.*"})
//@EnableTransactionManagement
@Import(CartRegAppRepositoryFactory.class)
public class CartRegAppServiceFactory {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Bean
    public PlaceService placeService() {
        log.info("Init PlaceService");
        return new PlaceServiceDAOImpl();
    }

    @Bean
    public CartTypeService cartTypeService() {
        log.info("Init CartTypeService");
        return new CartTypeServiceDAOImpl();
    }

    @Bean
    public CartSetService cartSetService() {
        log.info("Init CartSetService");
        return new CartSetServiceDAOImpl();
    }

    @Bean
    public CartSetChangeService cartSetChangeService() {
        log.info("Init CartSetChangeService");
        return new CartSetChangeServiceDAOImpl();
    }


//    @Bean
//    public PlatformTransactionManager txManager() {
//        return new HibernateTransactionManager(sessionFactory());
//    }


}
