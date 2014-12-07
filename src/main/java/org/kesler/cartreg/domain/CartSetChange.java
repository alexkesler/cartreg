package org.kesler.cartreg.domain;

import org.kesler.cartreg.dao.AbstractEntity;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Изменение
 */
@Entity
public class CartSetChange extends AbstractEntity{

    @Column(name="UUID",length=36)
    private String uuid = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    private Type type = Type.UNDEF;

    @ManyToOne
    @JoinColumn(name = "CartTypeID", nullable = false)
    private CartType cartType;

    @ManyToOne
    @JoinColumn(name = "FromPlaceID")
    private Place fromPlace;

    @ManyToOne
    @JoinColumn(name = "ToPlaceID")
    private Place toPlace;

    @Enumerated(EnumType.STRING)
    private CartStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private CartStatus toStatus;

    @Column(name = "Quantity")
    private Integer quantity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    public String getUuid() { return uuid; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getTypeString() {return type.getDesc();}

    public CartType getCartType() { return cartType; }
    public void setCartType(CartType cartType) { this.cartType = cartType; }
    public String getCartModel() {return cartType==null?"Не опр":cartType.getModel();}

    public Place getFromPlace() { return fromPlace; }
    public void setFromPlace(Place fromPlace) { this.fromPlace = fromPlace; }
    public String getFromPlaceString() {return fromPlace==null?"Не опр":fromPlace.getCommonName();}

    public Place getToPlace() { return toPlace; }
    public void setToPlace(Place toPlace) { this.toPlace = toPlace; }
    public String getToPlaceString() {return toPlace==null?"Не опр":toPlace.getCommonName();}

    public CartStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(CartStatus fromStatus) { this.fromStatus = fromStatus; }
    public String getFromStatusString() { return fromStatus==null?"Не опр": fromStatus.getDesc();}

    public CartStatus getToStatus() { return toStatus; }
    public void setToStatus(CartStatus toStatus) { this.toStatus = toStatus; }
    public String getToStatusString() { return toStatus==null?"Не опр": toStatus.getDesc();}

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }
    public String getChangeDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return changeDate==null?"Не опр":dateFormat.format(changeDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartSetChange that = (CartSetChange) o;

        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public enum Type {
        UNDEF("Не опр"),
        ARRIVAL("Поступление"),
        MOVE("Перемещение"),
        RECIEVE("Прием"),
        SEND("Передача"),
        FILL("Заправка"),
        DEFECT("Дефектовка"),
        WITHDRAW("Списание");

        private String desc;

        Type(String desc) {
            this.desc = desc;
        }

        public String getDesc() { return desc; }

    }
}
