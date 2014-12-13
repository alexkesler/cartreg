package org.kesler.cartreg.gui.cartsetreestr;

import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.gui.place.PlaceComparator;

import java.util.Comparator;

public class CartSetComparator implements Comparator<CartSet> {
    @Override
    public int compare(CartSet o1, CartSet o2) {
        int placeCompare = new PlaceComparator().compare(o1.getPlace(),o2.getPlace());
        int statusCompare = (o1.getStatus()==o2.getStatus())?0:
                (o1.getStatus()==CartStatus.NONE && o2.getStatus()!=CartStatus.NONE)?1:
                        Integer.signum(o1.getStatus().ordinal()-o2.getStatus().ordinal());
        int typeStringCompare = o1.getTypeString().compareTo(o2.getTypeString());




        return placeCompare*100 + statusCompare*10 + typeStringCompare;
    }
}
