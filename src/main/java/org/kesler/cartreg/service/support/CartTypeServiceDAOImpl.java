package org.kesler.cartreg.service.support;

import org.kesler.cartreg.dao.CartTypeDAO;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.service.CartTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Сервис типов картриджей
 */
@Transactional
@Service("cartTypeService")
public class CartTypeServiceDAOImpl implements CartTypeService {

    @Autowired
    private CartTypeDAO cartTypeDAO;

    @Transactional(readOnly = true)
    @Override
    public Collection<CartType> getAllCartTypes() {
        return cartTypeDAO.getAllCartTypes();
    }


    @Override
    public void addCartType(CartType cartType) {
        cartTypeDAO.addCartType(cartType);
    }


    @Override
    public void updateCartType(CartType cartType) {
        cartTypeDAO.updateCartType(cartType);
    }


    @Override
    public void removeCartType(CartType cartType) {
        cartTypeDAO.removeCartType(cartType);
    }
}
