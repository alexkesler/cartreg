package org.kesler.cartreg;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Конфигурация для сохранения данных
 */
@Configuration
//@ComponentScan({"org.kesler.dao.*"})
//@PropertySource(value="file:config/Data.properties")
public class CartRegAppRepositoryFactory {

    /**
     * Spring will wire this up to the properties file defined by the @PropertySource definition on this class. This
     * allows us to get access to our configuration properties in a fairly clean way.
     */
//    @Autowired
//    private Environment env;



}
