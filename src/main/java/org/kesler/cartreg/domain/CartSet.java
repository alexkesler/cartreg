package org.kesler.cartreg.domain;

import org.kesler.cartreg.dao.AbstractEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * Класс для набора картриджей
 */
@Entity
public class CartSet extends AbstractEntity{

    @Column(name="UUID",length=36)
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "CartTypeID", nullable = false)
    private CartType type;

    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @Column(name = "Quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "PlaceID")
    private Place place;

    public CartSet() {
        status = CartStatus.NONE;
    }

    public String getUuid() { return uuid; }

    public CartType getType() { return type; }
    public void setType(CartType type) { this.type = type; }

    public String getTypeString() {
        return type==null?"Не определена":type.getModel();
    }

    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }
    public String getStatusDesc() {
        return status.getDesc();
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }
    public String getPlaceString() {return place.getCommonName();}


    public boolean mergeCardSet(CartSet cartSet) {

        if (cartSet==null) return false;
        if (!type.equals(cartSet.getType())) return false;
        if (!status.equals(cartSet.getStatus())) return false;
        if (!place.equals(cartSet.getPlace())) return false;

        quantity += cartSet.getQuantity();

        return true;
    }

    public CartSet copyCartSet() {
        CartSet newCartSet = new CartSet();

        newCartSet.setPlace(place);
        newCartSet.setType(type);
        newCartSet.setStatus(status);
        newCartSet.setQuantity(quantity);

        return newCartSet;
    }

    public boolean deductCartSet (CartSet cartSet) {
        if (!type.equals(cartSet.getType())) return false;
        if (!status.equals(cartSet.getStatus())) return false;
        if (!place.equals(cartSet.getPlace())) return false;
        if (quantity < cartSet.getQuantity()) return false;

        quantity -= cartSet.getQuantity();

        return true;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartSet cartSet = (CartSet) o;

        if (!uuid.equals(cartSet.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
