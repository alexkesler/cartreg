package org.kesler.cartreg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Конфигурация для сохранения данных
 */
@Configuration
//@PropertySource(value="file:config/Data.properties")
public class CartRegAppRepositoryFactory {
    /**
     * Spring will wire this up to the properties file defined by the @PropertySource definition on this class. This
     * allows us to get access to our configuration properties in a fairly clean way.
     */
//    @Autowired
//    private Environment env;

    public @Bean
    DataSource dataSource() {
       return null;
    }

}
