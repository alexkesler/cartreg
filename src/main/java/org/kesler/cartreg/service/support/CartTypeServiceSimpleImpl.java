package org.kesler.cartreg.service.support;

import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.service.CartTypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * простая реализация
 */
public class CartTypeServiceSimpleImpl implements CartTypeService {
    List<CartType> cartTypes = new ArrayList<CartType>();

    @Override
    public Collection<CartType> getAllCartTypes() {
        return cartTypes;
    }

    @Override
    public void addCartType(CartType cartType) {
        cartTypes.add(cartType);
    }

    @Override
    public void updateCartType(CartType cartType) {

    }

    @Override
    public void removeCartType(CartType cartType) {
        cartTypes.remove(cartType);
    }
}
