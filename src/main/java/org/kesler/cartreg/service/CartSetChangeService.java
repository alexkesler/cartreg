package org.kesler.cartreg.service;

import org.kesler.cartreg.domain.CartSetChange;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by alex on 26.11.14.
 */
@Component
public interface CartSetChangeService {
    public Collection<CartSetChange> getAllChanges();
    public void addChange(CartSetChange change);
    public void updateChange(CartSetChange change);
    public void removeChange(CartSetChange change);
}
