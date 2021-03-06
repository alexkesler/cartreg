package org.kesler.cartreg;

import javafx.fxml.FXMLLoader;

import org.kesler.cartreg.gui.about.AboutController;
import org.kesler.cartreg.gui.arrival.ArrivalController;
import org.kesler.cartreg.gui.cartsetchanges.CartSetChangesController;
import org.kesler.cartreg.gui.cartsetreestr.CartSetReestrController;
import org.kesler.cartreg.gui.filling.FillingController;
import org.kesler.cartreg.gui.move.MoveController;
import org.kesler.cartreg.gui.place.PlaceController;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.cartset.CartSetController;
import org.kesler.cartreg.gui.carttype.CartTypeController;
import org.kesler.cartreg.gui.carttype.CartTypeListController;
import org.kesler.cartreg.gui.main.MainController;
import org.kesler.cartreg.gui.exchange.ExchangeController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.gui.placecartsetsselect.PlaceCartSetsSelectController;
import org.kesler.cartreg.gui.util.QuantityController;

import org.kesler.cartreg.gui.withdraw.WithdrawController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

/**
 * A Spring configuration class that provides factory methods for all the main components that make up the JavaFX client
 * application. This is used by the main App class on startup to load beans in a way that allows them to be autowired
 * into each other appropriately, along with all the other benefits of using Spring.
 * <p/>
 * Unless otherwise marked, all beans provided by this factory are singletons. So if you use an ApplicationContext for
 * loading this factory (as is done in the main App of this project), then each call to get a bean will return the same
 * instance. For example, the RestTemplate created below may get used in multiple services but only one instance will
 * ever be created and shared.
 * <p/>
 * This class is a direct replacement for the normal Spring XML file so in client side JavaFX we don't need this the
 * XML configuration file at all, just this.
 * <p/>
 * For more information on Spring configuration see:
 * http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/beans.html#beans-java
 */
@Configuration
@Import(CartRegAppServiceFactory.class)
public class CartRegAppFactory {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Factory method for creating a RestTemplate, which is a Spring helper class that simplifies making Rest calls onto
     * a remote server. See http://blog.springsource.com/2009/03/27/rest-in-spring-3-resttemplate/ for more information
     * on RestTemplates.
     *
     * @return a RestTemplate ready for use in the
     */

    @Bean
    public MainController mainController() {
        log.info("Load MainController");
        MainController mainController = loadController("/fxml/Main.fxml");
        return mainController;
    }

    @Bean
    public PlaceController placeController() {
        log.info("Load PlaceController");
        return loadController("/fxml/Place.fxml");
    }

    @Bean
    public PlaceListController branchListController() {
        log.info("Load PlaceListController");
        return loadController("/fxml/PlaceList.fxml");
    }

    @Bean
    public CartTypeListController cartTypeListController() {
        log.info("Load CartTypeListController");
        return loadController("/fxml/CartTypeList.fxml");
    }

    @Bean
    public CartTypeController cartTypeController() {
        log.info("Load CartTypeController");
        return loadController("/fxml/CartType.fxml");
    }

    @Bean
    public ArrivalController arrivalController() {
        log.info("Load ArrivalController");
        return loadController("/fxml/Arrival.fxml");
    }

    @Bean
    public MoveController moveController() {
        log.info("Load MoveController");
        return loadController("/fxml/Move.fxml");
    }

    @Bean
    public CartSetController cartSetController() {
        log.info("Load CartSetController");
        return loadController("/fxml/CartSet.fxml");
    }

    @Bean
    public PlaceCartSetsController placeCartSetsController() {
        log.info("Load PlaceCartSetsController");
        return loadController("/fxml/PlaceCartSets.fxml");
    }

    @Bean
    public PlaceCartSetsSelectController placeCartSetsSelectController() {
        log.info("Load PlaceCartSetsSelectController");
        return loadController("/fxml/PlaceCartSetsSelect.fxml");
    }

    @Bean
    public ExchangeController exchangeController() {
        log.info("Load ExchangeController");
        return loadController("/fxml/Exchange.fxml");
    }

    @Bean
    public FillingController fillingController() {
        log.info("Load FillingController");
        return loadController("/fxml/Filling.fxml");
    }

    @Bean
    public QuantityController quantityController() {
        log.info("Load QuantityController");
        return loadController("/fxml/Quantity.fxml");
    }

    @Bean
    public CartSetChangesController cartSetChangesController() {
        log.info("Load CartSetChangesController");
        return loadController("/fxml/CartSetChanges.fxml");
    }

    @Bean
    public CartSetReestrController cartSetReestrController() {
        log.info("Load CartSetReestrController");
        return loadController("/fxml/CartSetReestr.fxml");
    }

    @Bean
    public WithdrawController withdrawController() {
        log.info("Load WithdrawController");
        return loadController("/fxml/Withdraw.fxml");
    }

    @Bean
    public AboutController aboutController() {
        log.info("Load AboutController");
        return loadController("/fxml/About.fxml");
    }

    /**
     * Convenience method for loading Controllers from FXML. FXML can be a little impure in its inter-dependencies
     * between client and server (it is quite biased to things being view driven and tightly couples the view into its
     * controller, etc). We make things a little cleaner by interacting mostly with the Controller and only accessing
     * the View via it. This load method hooks reverses the focus of the FXMLLoader to make things Controller based.
     *
     * @param fxmlFile the file to load the FXML from, this should be relative to the classpath.
     * @param <T> the type of Controller to return is inferred by whatever you assign the result of this method to.
     * @return the Controller loaded from the FXML specified which should have its view loaded and attached.
     */
    private <T> T loadController(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.load();
            return (T) loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to load FXML file '%s'", fxmlFile), e);
        }
    }
}
