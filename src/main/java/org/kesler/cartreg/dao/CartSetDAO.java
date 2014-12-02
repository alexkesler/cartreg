package org.kesler.cartreg.dao;

import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;

import java.util.Collection;

/**
 * Created by alex on 29.11.14.
 */
public interface CartSetDAO {
    public void addCartSet(CartSet cartSet);
    public void updateCartSet(CartSet cartSet);
    public void removeCartSet(CartSet cartSet);
    public Collection<CartSet> getAllCartSets();
    public Collection<CartSet> getCartSetsByPlace(Place place);

}
