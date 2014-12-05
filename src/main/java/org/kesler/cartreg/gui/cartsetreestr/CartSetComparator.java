package org.kesler.cartreg.gui.cartsetreestr;

import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.gui.place.PlaceComparator;

import java.util.Comparator;

/**
 * Created by alex on 27.11.14.
 */
public class CartSetComparator implements Comparator<CartSet> {
    @Override
    public int compare(CartSet o1, CartSet o2) {
        int placeCompare = new PlaceComparator().compare(o1.getPlace(),o2.getPlace());

        if (o1.getStatus()==o2.getStatus()) return placeCompare;
        if (o1.getStatus()==CartStatus.NONE && o2.getStatus()!=CartStatus.NONE) return 1 + placeCompare*10;


        return Integer.signum(o1.getStatus().ordinal()-o2.getStatus().ordinal()) + placeCompare*10;
    }
}
