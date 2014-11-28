package org.kesler.cartreg.gui.cartsetreestr;

import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.gui.place.PlaceComparator;

import java.util.Comparator;

/**
 * Created by alex on 27.11.14.
 */
public class CartSetComparator implements Comparator<CartSet> {
    @Override
    public int compare(CartSet o1, CartSet o2) {
        return new PlaceComparator().compare(o1.getPlace(),o2.getPlace());
    }
}
