package org.kesler.cartreg.dao;

import org.kesler.cartreg.domain.CartType;

import java.util.Collection;

/**
 * Created by alex on 29.11.14.
 */
public interface CartTypeDAO {
    public void addCartType(CartType cartType);
    public void updateCartType(CartType cartType);
    public void removeCartType(CartType cartType);
    public Collection<CartType> getAllCartTypes();
}
