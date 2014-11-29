package org.kesler.cartreg.service;

import org.kesler.cartreg.domain.CartType;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Сервис по управлению моделями картриджей
 */

public interface CartTypeService {
    public Collection<CartType> getAllCartTypes();
    public void addCartType(CartType cartType);
    public void updateCartType(CartType cartType);
    public void removeCartType(CartType cartType);
}
