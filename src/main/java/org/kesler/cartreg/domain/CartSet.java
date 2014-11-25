package org.kesler.cartreg.domain;

import java.util.UUID;

/**
 * Класс для набора картриджей
 */
public class CartSet {

    private String uuid = UUID.randomUUID().toString();
    private CartType type;
    private CartStatus status;
    private Integer quantity;
    private Place place;

    public CartSet() {
        status = CartStatus.NONE;
    }

    public CartType getType() { return type; }
    public void setType(CartType type) { this.type = type; }

    public String getModel() {
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

    public boolean mergeCardSet(CartSet cartSet) {

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
