package org.kesler.cartreg.dao;

import org.kesler.cartreg.domain.Place;

import java.util.Collection;

/**
 * Created by alex on 29.11.14.
 */
public interface PlaceDAO {
    public void addPlace(Place place);
    public void updatePlace(Place place);
    public void removePlace(Place place);
    public Collection<Place> getAllPlaces();
    public Collection<Place> getDirects();
    public Collection<Place> getStorages();
    public Collection<Place> getBranches();
}
