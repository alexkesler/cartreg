package org.kesler.cartreg.service.support;

import org.kesler.cartreg.dao.PlaceDAO;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by alex on 01.12.14.
 */
@Service("placeService")
public class PlaceServiceDAOImpl implements PlaceService {

    @Autowired
    private PlaceDAO placeDAO;

    @Transactional(readOnly = true)
    @Override
    public Collection<Place> getAllPlaces() {
        return placeDAO.getAllPlaces();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Place> getDirects() {
        return placeDAO.getDirects();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Place> getStorages() {
        return placeDAO.getStorages();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Place> getBranches() {
        return placeDAO.getBranches();
    }

    @Transactional
    @Override
    public void addPlace(Place place) {
        placeDAO.addPlace(place);
    }

    @Transactional
    @Override
    public void updatePlace(Place place) {
        placeDAO.updatePlace(place);
    }

    @Transactional
    @Override
    public void removePlace(Place place) {
        placeDAO.removePlace(place);
    }
}
