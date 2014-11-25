package org.kesler.cartreg.service;

import org.kesler.cartreg.domain.Filling;

import java.util.Collection;

/**
 * Служба учета заправок
 */
public interface FillingService {

    public Collection<Filling> getAllFillings();
    public void addFilling(Filling filling);
    public void updateFilling(Filling filling);
    public void removeFilling(Filling filling);

}
