package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.kesler.cartreg.dao.CartTypeDAO;
import org.kesler.cartreg.domain.CartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Repository
public class CartTypeDAOImpl implements CartTypeDAO {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void addCartType(CartType cartType) {
        sessionFactory.getCurrentSession().save(cartType);
    }

    @Override
    public void updateCartType(CartType cartType) {
        sessionFactory.getCurrentSession().update(cartType);
    }

    @Override
    public void removeCartType(CartType cartType) {
        sessionFactory.getCurrentSession().delete(cartType);
    }

    @Transactional
    @Override
    public Collection<CartType> getAllCartTypes() {
        return sessionFactory.getCurrentSession()
                .createCriteria(CartType.class)
                .list();
    }
}
