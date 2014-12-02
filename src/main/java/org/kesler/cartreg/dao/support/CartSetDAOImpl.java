package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.kesler.cartreg.dao.CartSetDAO;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Repository
public class CartSetDAOImpl implements CartSetDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addCartSet(CartSet cartSet) {
        sessionFactory.getCurrentSession().save(cartSet);
    }

    @Override
    public void updateCartSet(CartSet cartSet) {
        sessionFactory.getCurrentSession().update(cartSet);
    }

    @Override
    public void removeCartSet(CartSet cartSet) {
        sessionFactory.getCurrentSession().delete(cartSet);
    }

    @Override
    public Collection<CartSet> getAllCartSets() {
        return sessionFactory.getCurrentSession()
                .createCriteria(CartSet.class)
                .list();
    }

    @Override
    public Collection<CartSet> getCartSetsByPlace(Place place) {
        return sessionFactory.getCurrentSession()
                .createCriteria(CartSet.class)
                .add(Restrictions.eq("place",place))
                .list();
    }
}
