package org.kesler.cartreg.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Заправка картриджей
 */
public class Filling {

    private String uuid = UUID.randomUUID().toString();
    private CartType cartType;
    private Integer quantity;
    private Date date;

    public CartType getCartType() { return cartType; }
    public void setCartType(CartType cartType) { this.cartType = cartType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filling filling = (Filling) o;

        if (!uuid.equals(filling.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
