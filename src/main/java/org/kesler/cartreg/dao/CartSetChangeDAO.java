package org.kesler.cartreg.dao;

import org.kesler.cartreg.domain.CartSetChange;

import java.util.Collection;

/**
 * Created by alex on 29.11.14.
 */
public interface CartSetChangeDAO {
    public void addChange(CartSetChange change);
    public void removeChange(CartSetChange change);
    public Collection<CartSetChange> getAllChanges();
}
