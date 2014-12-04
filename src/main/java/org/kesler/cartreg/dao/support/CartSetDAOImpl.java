package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.kesler.cartreg.dao.CartSetDAO;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 01.12.14.
 */

@Repository
public class CartSetDAOImpl implements CartSetDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addCartSet(CartSet cartSet) {
        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().save(cartSet);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public void updateCartSet(CartSet cartSet) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().update(cartSet);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public void removeCartSet(CartSet cartSet) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(cartSet);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public Collection<CartSet> getAllCartSets() {
        List<CartSet> cartSets = new ArrayList<CartSet>();

        sessionFactory.getCurrentSession().beginTransaction();
        cartSets = sessionFactory.getCurrentSession()
                .createCriteria(CartSet.class)
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();

        return cartSets;
    }

    @Override
    public Collection<CartSet> getCartSetsByPlace(Place place) {
        List<CartSet> cartSets = new ArrayList<CartSet>();

        sessionFactory.getCurrentSession().beginTransaction();
        cartSets = sessionFactory.getCurrentSession()
                .createCriteria(CartSet.class)
                .add(Restrictions.eq("place",place))
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();

        return cartSets;
    }
}
