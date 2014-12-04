package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.kesler.cartreg.dao.CartTypeDAO;
import org.kesler.cartreg.domain.CartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().save(cartType);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public void updateCartType(CartType cartType) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().update(cartType);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public void removeCartType(CartType cartType) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(cartType);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Transactional
    @Override
    public Collection<CartType> getAllCartTypes() {

        Collection<CartType> cartTypes = new ArrayList<CartType>();
        sessionFactory.getCurrentSession().beginTransaction();
        cartTypes = sessionFactory.getCurrentSession()
                .createCriteria(CartType.class)
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();

        return cartTypes;

    }
}
