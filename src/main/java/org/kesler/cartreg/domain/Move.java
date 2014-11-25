package org.kesler.cartreg.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Перемещение
 */
public class Move {

    private String uuid = UUID.randomUUID().toString();
    private Place fromPlace;
    private Place toPlace;
    private Date moveDate;
    private CartType cartType;
    private CartStatus cartStatus;
    private Integer quantity;

    public Place getFromPlace() { return fromPlace; }
    public void setFromPlace(Place fromPlace) { this.fromPlace = fromPlace; }

    public Place getToPlace() { return toPlace; }
    public void setToPlace(Place toPlace) { this.toPlace = toPlace; }

    public Date getMoveDate() { return moveDate; }
    public void setMoveDate(Date moveDate) { this.moveDate = moveDate; }

    public CartType getCartType() { return cartType; }
    public void setCartType(CartType cartType) { this.cartType = cartType; }

    public CartStatus getCartStatus() { return cartStatus; }
    public void setCartStatus(CartStatus cartStatus) { this.cartStatus = cartStatus; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (!uuid.equals(move.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
