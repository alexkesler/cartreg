package org.kesler.cartreg.gui.place;

import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.domain.PlaceType;

import java.util.Comparator;

/**
 * Компаратор для сортировки размещений
 */
public class PlaceComparator implements Comparator<Place> {

    @Override
    public int compare(Place o1, Place o2) {
        if (o1==null && o2==null) return 0;
        if (o1==null && o2!=null) return 1;
        if (o1!=null && o2==null) return -1;

        if (o1.equals(o2)) return 0;
        if (o1.getType()== PlaceType.DIRECT && o2.getType()!=PlaceType.DIRECT)  return -1;
        if (o1.getType()==PlaceType.STORAGE) {
            if (o2.getType()==PlaceType.DIRECT) return 1;
            if (o2.getType()==PlaceType.BRANCH) return -1;
        }
        if (o1.getType()==PlaceType.BRANCH && o2.getType()!=PlaceType.BRANCH) return 1;

        if (o1.getType()==o2.getType()) return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());


        return 0;
    }

}
