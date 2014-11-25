package org.kesler.cartreg.service;

import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;

import java.util.Collection;

public interface CartSetService {
    public Collection<CartSet> getAllCartSets();
    public Collection<CartSet> getCartSetsByPlace(Place place);
    public void addCartSet(CartSet cartSet);
    public void updateCartSet(CartSet cartSet);
    public void removeCartSet(CartSet cartSet);
}
