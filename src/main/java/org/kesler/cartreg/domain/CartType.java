package org.kesler.cartreg.domain;

import java.util.UUID;

/**
 * Модель картриджа
 */
public class CartType {

    private String uuid = UUID.randomUUID().toString();
    private String model;

    public String getUuid() { return uuid; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartType cartType = (CartType) o;

        if (!uuid.equals(cartType.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
