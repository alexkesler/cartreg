package org.kesler.cartreg.dao.support;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.kesler.cartreg.dao.PlaceDAO;
import org.kesler.cartreg.domain.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Repository
public class PlaceDAOImpl implements PlaceDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addPlace(Place place) {

        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().save(place);
        sessionFactory.getCurrentSession().getTransaction().commit();

    }

    @Override
    public void updatePlace(Place place) {
        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().update(place);
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Override
    public void removePlace(Place place) {
        sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().delete(place);
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Override
    public Collection<Place> getAllPlaces() {
        Collection<Place> places = new ArrayList<Place>();
        sessionFactory.getCurrentSession().beginTransaction();
        places = sessionFactory.getCurrentSession().createCriteria(Place.class).list();
        sessionFactory.getCurrentSession().getTransaction().commit();
        return places;
    }

    @Override
    public Collection<Place> getDirects() {
        Collection<Place> places = new ArrayList<Place>();
        sessionFactory.getCurrentSession().beginTransaction();
        places = sessionFactory.getCurrentSession().createCriteria(Place.class)
                .add(Restrictions.eq("type", Place.Type.DIRECT))
                .list();
        sessionFactory.getCurrentSession().getTransaction().commit();

        return places;
    }

    @Override
    public Collection<Place> getStorages() {
        return sessionFactory.getCurrentSession().createCriteria(Place.class)
                .add(Restrictions.eq("type", Place.Type.STORAGE))
                .list();
    }

    @Override
    public Collection<Place> getBranches() {
        return sessionFactory.getCurrentSession().createCriteria(Place.class)
                .add(Restrictions.eq("type", Place.Type.BRANCH))
                .list();
    }
}
