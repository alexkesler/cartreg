package org.kesler.cartreg.service.support;

import org.kesler.cartreg.dao.CartSetDAO;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.service.CartSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Service("cartSetService")
public class CartSetServiceDAOImpl implements CartSetService {

    @Autowired
    private CartSetDAO cartSetDAO;

    @Transactional(readOnly = true)
    @Override
    public Collection<CartSet> getAllCartSets() {
        return cartSetDAO.getAllCartSets();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<CartSet> getCartSetsByPlace(Place place) {
        return cartSetDAO.getCartSetsByPlace(place);
    }

    @Transactional
    @Override
    public void addCartSet(CartSet cartSet) {
        cartSetDAO.addCartSet(cartSet);
    }

    @Transactional
    @Override
    public void updateCartSet(CartSet cartSet) {
        if (cartSet.getQuantity()==0) {
            cartSetDAO.removeCartSet(cartSet);
        } else {
            cartSetDAO.updateCartSet(cartSet);
        }

    }

    @Transactional
    @Override
    public void removeCartSet(CartSet cartSet) {
        cartSetDAO.removeCartSet(cartSet);
    }
}
