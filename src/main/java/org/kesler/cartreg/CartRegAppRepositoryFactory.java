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
import org.kesler.cartreg.util.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
@PropertySource(value={"classpath:config/CartReg.properties","file:config/CartReg.properties"},ignoreResourceNotFound = true)
public class CartRegAppRepositoryFactory {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Spring will wire this up to the properties file defined by the @PropertySource definition on this class. This
     * allows us to get access to our configuration properties in a fairly clean way.
     */
    @Autowired
    private Environment env;

//    @Bean
//    public static PropertyPlaceholderConfigurer ppc () {
//        return new PropertyPlaceholderConfigurer();
//    }
//
//    @Value("${server.ip:10.10.0.170}")
//    private String serverIp;
//
//    @Value("${server.db:cartreg}")
//    private String serverDb;
//
//    @Value("${server.user:croper}")
//    private String user;
//
//    @Value("${server.password:Qwerty123}")
//    private String password;


    @Bean
    public DataSource dataSource() {
        log.info("Init DataSource");
        DriverManagerDataSource ds = new DriverManagerDataSource();

//        ds.setDriverClassName("com.mysql.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://" + serverIp + ":3306/"+serverDb);
//        ds.setUsername(user);
//        ds.setPassword(password);

        return ds;
    }


    private Properties getHibernateProperties() {

        String serverIp = env.getProperty("server.ip");
        String serverDb = env.getProperty("server.db");
        String user = env.getProperty("server.user");
        String password = env.getProperty("server.password");

        Options.addOption("server.ip",serverIp);
        Options.addOption("server.db",serverDb);

        String url = "jdbc:mysql://" + serverIp + ":3306/"+serverDb;
        log.info("Get Hibernate properties, url: " + url + " user: " + user + " password: " + password);

        Properties prop = new Properties();
        prop.put("hibernate.connection.driver_class","com.mysql.jdbc.Driver");
        prop.put("hibernate.connection.url",url);
        prop.put("hibernate.connection.username",user);
        prop.put("hibernate.connection.password",password);
//        prop.put("hibernate.connection.pool_size","1");
        prop.put("hibernate.c3p0.min_size", "5");
        prop.put("hibernate.c3p0.max_size", "20");
        prop.put("hibernate.format_sql", "false");
        prop.put("hibernate.show_sql", "false");
        prop.put("hibernate.hbm2ddl.auto","update");
        prop.put("hibernate.dialect",
                "org.hibernate.dialect.MySQL5Dialect");
        prop.put("hibernate.current_session_context_class","thread");


        return prop;
    }


    @Bean
    public SessionFactory sessionFactory() {
        log.info("Init SessionFactory");

        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());

        builder.scanPackages("org.kesler.cartreg.domain","org.kesler.cartreg.dao");
        builder.setProperties(getHibernateProperties());

        return builder.buildSessionFactory();
    }


    @Bean
    public PlaceDAO placeDAO() {
        log.info("Init PlaceDAO");
        return new PlaceDAOImpl();
    }

    @Bean
    public CartTypeDAO cartTypeDAO() {
        log.info("Init CartTypeDAO");
        return new CartTypeDAOImpl();
    }

    @Bean
    public CartSetDAO cartSetDAO() {
        log.info("Init CartSetDAO");
        return new CartSetDAOImpl();
    }

    @Bean
    public CartSetChangeDAO cartSetChangeDAO() {
        log.info("Init CartSetChangeDAO");
        return new CartSetChangeDAOImpl();
    }



}
