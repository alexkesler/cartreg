package org.kesler.cartreg.service.support;

import org.kesler.cartreg.domain.Filling;
import org.kesler.cartreg.service.FillingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Простая реализация
 */
public class FillingServiceSimpleImpl implements FillingService {
    private List<Filling> fillings = new ArrayList<Filling>();

    @Override
    public Collection<Filling> getAllFillings() {
        return fillings;
    }

    @Override
    public void addFilling(Filling filling) {
        fillings.add(filling);
    }

    @Override
    public void updateFilling(Filling filling) {

    }

    @Override
    public void removeFilling(Filling filling) {
        fillings.remove(filling);
    }
}
