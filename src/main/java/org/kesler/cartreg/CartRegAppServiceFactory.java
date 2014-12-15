package org.kesler.cartreg;


import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.CartTypeService;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.service.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;


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
