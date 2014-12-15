package org.kesler.cartreg.gui.placecartsetsselect;

import org.kesler.cartreg.gui.cartsetreestr.CartSetComparator;

import java.util.Comparator;

public class CheckableCartSetComparator implements Comparator<CheckableCartSet> {
    @Override
    public int compare(CheckableCartSet o1, CheckableCartSet o2) {
        return new CartSetComparator().compare(o1.getSourceCartSet(),o2.getSourceCartSet());
    }
}
