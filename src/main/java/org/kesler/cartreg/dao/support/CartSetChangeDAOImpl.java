package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.kesler.cartreg.dao.CartSetChangeDAO;
import org.kesler.cartreg.domain.CartSetChange;
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
public class CartSetChangeDAOImpl implements CartSetChangeDAO {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addChange(CartSetChange change) {
        log.debug("Adding change..");

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().save(change);
        sessionFactory.getCurrentSession().getTransaction().commit();

        log.debug("Adding change complete");

    }

    @Override
    public void removeChange(CartSetChange change) {
        log.debug("Removing change..");

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(change);
        sessionFactory.getCurrentSession().getTransaction().commit();

        log.debug("Removing change complete");
    }

    @Override
    public Collection<CartSetChange> getAllChanges() {
        log.debug("Receiving all changes..");

        List<CartSetChange> changes = new ArrayList<CartSetChange>();

        sessionFactory.getCurrentSession().beginTransaction();
        changes = sessionFactory.getCurrentSession()
                .createCriteria(CartSetChange.class)
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();

        log.debug("Receive "+ changes.size() + " changes");

        return changes;
    }
}
