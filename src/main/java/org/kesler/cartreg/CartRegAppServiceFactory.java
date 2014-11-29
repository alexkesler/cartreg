package org.kesler.cartreg;

import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.CartTypeService;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.service.support.CartSetChangeServiceSimpleImpl;
import org.kesler.cartreg.service.support.CartSetServiceSimpleImpl;
import org.kesler.cartreg.service.support.CartTypeServiceSimpleImpl;
import org.kesler.cartreg.service.support.PlaceServiceSimpleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by alex on 29.11.14.
 */
@Configuration
@Import(CartRegAppRepositoryFactory.class)
public class CartRegAppServiceFactory {


    @Bean
    public PlaceService placeService() {
        return new PlaceServiceSimpleImpl();
    }

    @Bean
    public CartTypeService cartTypeService() {
        return new CartTypeServiceSimpleImpl();
    }

    @Bean
    public CartSetService cartSetService() {
        return new CartSetServiceSimpleImpl();
    }

    @Bean
    public CartSetChangeService cartSetChangeService() {
        return new CartSetChangeServiceSimpleImpl();
    }


}
