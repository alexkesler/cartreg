package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.kesler.cartreg.dao.CartSetChangeDAO;
import org.kesler.cartreg.domain.CartSetChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Repository
public class CartSetChangeDAOImpl implements CartSetChangeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addChange(CartSetChange change) {
        sessionFactory.getCurrentSession().save(change);
    }

    @Override
    public void removeChange(CartSetChange change) {
        sessionFactory.getCurrentSession().delete(change);
    }

    @Override
    public Collection<CartSetChange> getAllChanges() {
        return sessionFactory.getCurrentSession()
                .createCriteria(CartSetChange.class)
                .list();
    }
}
