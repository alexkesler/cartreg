package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.kesler.cartreg.dao.CartSetChangeDAO;
import org.kesler.cartreg.domain.CartSetChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 01.12.14.
 */
@Repository
public class CartSetChangeDAOImpl implements CartSetChangeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addChange(CartSetChange change) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().save(change);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public void removeChange(CartSetChange change) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(change);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public Collection<CartSetChange> getAllChanges() {
        List<CartSetChange> changes = new ArrayList<CartSetChange>();

        sessionFactory.getCurrentSession().beginTransaction();
        changes = sessionFactory.getCurrentSession()
                .createCriteria(CartSetChange.class)
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();

        return changes;
    }
}
