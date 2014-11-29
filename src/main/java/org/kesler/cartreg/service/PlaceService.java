package org.kesler.cartreg.service;


import org.kesler.cartreg.domain.Place;

import java.util.Collection;


public interface PlaceService {

    public Collection<Place> getAllPlaces();
    public Collection<Place> getDirects();
    public Collection<Place> getStorages();
    public Collection<Place> getBranches();
    public void addPlace(Place place);
    public void updatePlace(Place place);
    public void removePlace(Place place);

}
