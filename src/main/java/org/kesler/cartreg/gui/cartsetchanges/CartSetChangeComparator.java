package org.kesler.cartreg.gui.cartsetchanges;

import org.kesler.cartreg.domain.CartSetChange;

import java.util.Comparator;

public class CartSetChangeComparator implements Comparator<CartSetChange> {
    @Override
    public int compare(CartSetChange o1, CartSetChange o2) {

        return o1.getChangeDate().compareTo(o2.getChangeDate());
    }

}
