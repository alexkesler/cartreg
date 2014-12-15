package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.kesler.cartreg.dao.CartSetDAO;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addCartSet(CartSet cartSet) {
        log.debug("Adding CartSet: "
                + cartSet.getTypeString()
                + " (" + cartSet.getStatusDesc() + ") - "
                + cartSet.getQuantity());
        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().save(cartSet);
        sessionFactory.getCurrentSession().getTransaction().commit();
        log.debug("Adding CartSet complete");

    }

    @Override
    public void updateCartSet(CartSet cartSet) {
        log.debug("Updating CartSet "
                + cartSet.getTypeString()
                + " (" + cartSet.getStatusDesc() + ") - "
                + cartSet.getQuantity());

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().update(cartSet);
        sessionFactory.getCurrentSession().getTransaction().commit();
        log.debug("Updating CartSet complete");
    }

    @Override
    public void removeCartSet(CartSet cartSet) {
        log.debug("Removing CartSet "
                + cartSet.getTypeString()
                + " (" + cartSet.getStatusDesc() + ") - "
                + cartSet.getQuantity());

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(cartSet);
        sessionFactory.getCurrentSession().getTransaction().commit();
        log.debug("Removing CartSet complete");

    }

    @Override
    public Collection<CartSet> getAllCartSets() {
        List<CartSet> cartSets = new ArrayList<CartSet>();

        log.debug("Receiving all CartSets");
        sessionFactory.getCurrentSession().beginTransaction();
        cartSets = sessionFactory.getCurrentSession()
                .createCriteria(CartSet.class)
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();
        log.debug("Receive " + cartSets.size() + " CartSets");

        return cartSets;
    }

    @Override
    public Collection<CartSet> getCartSetsByPlace(Place place) {
        List<CartSet> cartSets = new ArrayList<CartSet>();

        log.debug("Receiving CartSets by Place: " + place.getCommonName());
        sessionFactory.getCurrentSession().beginTransaction();
        cartSets = sessionFactory.getCurrentSession()
                .createCriteria(CartSet.class)
                .add(Restrictions.eq("place",place))
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();
        log.debug("Receive " + cartSets.size() + " CartSets");

        return cartSets;
    }
}
