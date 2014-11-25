package org.kesler.cartreg.service.support;

import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.domain.PlaceType;
import org.kesler.cartreg.service.PlaceService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Простая реализация сервиса филиалов
 */
public class PlaceServiceSimpleImpl implements PlaceService {

    private final List<Place> places = new ArrayList<Place>();


    @Override
    public Collection<Place> getAllPlaces() {
        return places;
    }

    @Override
    public Collection<Place> getDirects() {
        List<Place> directs = new ArrayList<Place>();

        for (Place place: places) {
            if (place.getType().equals(PlaceType.DIRECT)) directs.add(place);
        }

        return directs;
    }

    @Override
    public Collection<Place> getStorages() {
        List<Place> storages = new ArrayList<Place>();

        for (Place place: places) {
            if (place.getType().equals(PlaceType.STORAGE)) storages.add(place);
        }

        return storages;
    }

    @Override
    public Collection<Place> getBranches() {
        List<Place> storages = new ArrayList<Place>();

        for (Place place: places) {
            if (place.getType().equals(PlaceType.BRANCH)) storages.add(place);
        }

        return storages;
    }

    @Override
    public void addPlace(Place place) {
        places.add(place);
    }

    @Override
    public void removePlace(Place place) {
        places.remove(place);
    }

    @Override
    public void updatePlace(Place place) {

    }
}
