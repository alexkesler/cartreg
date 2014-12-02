package org.kesler.cartreg.service.support;

import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.service.CartSetChangeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Простая реализация
 */
public class CartSetChangeServiceSimpleImpl implements CartSetChangeService {
    private List<CartSetChange> changes = new ArrayList<CartSetChange>();

    @Override
    public Collection<CartSetChange> getAllChanges() {
        return changes;
    }

    @Override
    public void addChange(CartSetChange change) {
        changes.add(change);
    }

    @Override
    public void removeChange(CartSetChange change) {
        changes.remove(change);
    }
}
