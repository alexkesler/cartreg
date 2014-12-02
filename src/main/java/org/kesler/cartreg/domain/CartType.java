package org.kesler.cartreg.domain;

import org.kesler.cartreg.dao.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

/**
 * Модель картриджа
 */
@Entity
public class CartType extends AbstractEntity{

    @Column(name="UUID",length=36)
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "Model")
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
