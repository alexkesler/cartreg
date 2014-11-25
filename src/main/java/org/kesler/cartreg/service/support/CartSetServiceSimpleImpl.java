package org.kesler.cartreg.service.support;

import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.service.CartSetService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Служба по управлению наборами картриджей
 */
public class CartSetServiceSimpleImpl implements CartSetService {
    private Set<CartSet> cartSets = new HashSet<CartSet>();

    @Override
    public Collection<CartSet> getAllCartSets() {
        return cartSets;
    }

    @Override
    public Collection<CartSet> getCartSetsByPlace(Place place) {
        Set<CartSet> placeCartSets = new HashSet<CartSet>();

        for (CartSet cartSet: cartSets) {
            if (cartSet.getPlace().equals(place))
                placeCartSets.add(cartSet);
        }

        return placeCartSets;
    }

    @Override
    public void addCartSet(CartSet cartSet) {
        for(CartSet cs : cartSets) {
            if (cs.mergeCardSet(cartSet)) {
                return;
            }
        }
        cartSets.add(cartSet);
    }

    @Override
    public void updateCartSet(CartSet cartSet) {
        for(CartSet cs : cartSets) {
            if (!cs.equals(cartSet) && cs.mergeCardSet(cartSet)) {
                removeCartSet(cartSet);
                return;
            }
        }
        if (cartSet.getQuantity()==0) removeCartSet(cartSet);

    }

    @Override
    public void removeCartSet(CartSet cartSet) {
        cartSets.remove(cartSet);
    }
}
