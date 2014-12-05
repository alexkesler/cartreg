package org.kesler.cartreg.service.support;

import org.kesler.cartreg.dao.CartSetDAO;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.service.CartSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Service("cartSetService")
public class CartSetServiceDAOImpl implements CartSetService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CartSetDAO cartSetDAO;

    @Override
    public Collection<CartSet> getAllCartSets() {
        log.info("Receiving all CartSets...");

        return cartSetDAO.getAllCartSets();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<CartSet> getCartSetsByPlace(Place place) {
        log.info("Receiving all CartSets by Place: " + place.getCommonName() + " ..");
        return cartSetDAO.getCartSetsByPlace(place);
    }

    @Override
    public void addCartSet(CartSet cartSet) {
        log.info("Adding CartSet..");
        log.debug("Receiving all CartSets in place..");
        Collection<CartSet> cartSets = cartSetDAO.getCartSetsByPlace(cartSet.getPlace());
        log.debug("Receive " + cartSets.size() + " CartSets try to merge..");
        for (CartSet cs:cartSets) {
            if (cs.mergeCardSet(cartSet)) {
                log.debug("Merge occurred - update merged CartSet");
                cartSetDAO.updateCartSet(cs);
                return;
            }
        }
        log.debug("Add new CartSet");
        cartSetDAO.addCartSet(cartSet);
        log.info("Complete");
    }

    @Override
    public void updateCartSet(CartSet cartSet) {
        log.info("Updating CartSet..");
        log.debug("Receiving all CartSets in place..");
        Collection<CartSet> cartSets = cartSetDAO.getCartSetsByPlace(cartSet.getPlace());
        log.debug("Receive " + cartSets.size() + " CartSets try to merge..");
        for (CartSet cs:cartSets) {
            if (!cs.equals(cartSet) && cs.mergeCardSet(cartSet)) {
                log.debug("Merge occurred - update merged CartSet, remove updating CartSet..");
                cartSetDAO.updateCartSet(cs);
                cartSetDAO.removeCartSet(cartSet);
                log.info("Complete");
                return;
            }
        }

        if (cartSet.getQuantity()==0) {
            log.debug("Updating CartSet quantity=0 -> remove..");
            cartSetDAO.removeCartSet(cartSet);
            log.info("Complete");
            return;
        }

        cartSetDAO.updateCartSet(cartSet);
        log.info("Complete");

    }

    @Override
    public void removeCartSet(CartSet cartSet) {
        log.info("Removing CartSet..");
        cartSetDAO.removeCartSet(cartSet);
        log.info("Complete");
    }
}
