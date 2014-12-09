package org.kesler.cartreg.service;

import org.kesler.cartreg.domain.CartSetChange;

import java.util.Collection;

public interface CartSetChangeService {
    public Collection<CartSetChange> getAllChanges();
    public void addChange(CartSetChange change);
    public void removeChange(CartSetChange change);
}
