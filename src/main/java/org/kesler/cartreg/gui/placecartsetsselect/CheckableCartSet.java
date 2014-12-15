package org.kesler.cartreg.gui.placecartsetsselect;

import javafx.beans.property.*;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;

public class CheckableCartSet {
    private CartSet sourceCartSet;
    private BooleanProperty checked = new SimpleBooleanProperty(this,"checked",false);
    private IntegerProperty checkedQuantity = new SimpleIntegerProperty(this,"checkedQuantity",0);
    private StringProperty checkedQuantityString = new SimpleStringProperty(this,"checkedQuantityString","");

    public CheckableCartSet(CartSet sourceCartSet) {
        this.sourceCartSet = sourceCartSet;
        this.checkedQuantityString.bind(this.checkedQuantity.asString().concat(" из " + getSourceQuantity()));
    }

    public CheckableCartSet(CartSet sourceCartSet, Integer checkedQuantity, Boolean checked) {
        this.sourceCartSet = sourceCartSet;
        this.checkedQuantity.setValue(checkedQuantity);
        this.checked.setValue(checked);
        this.checkedQuantityString.bind(this.checkedQuantity.asString().concat(" из " + getSourceQuantity()));
    }

    // read-write properties
    public final BooleanProperty checkedProperty() {return checked;}
    public final Boolean getChecked() {return checked.getValue();}
    public final void setChecked(Boolean checked) {this.checked.setValue(checked);}

    public final IntegerProperty checkedQuantityProperty() {return checkedQuantity;}
    public final Integer getCheckedQuantity() {return checkedQuantity.getValue();}
    public final void setCheckedQuantity(Integer checkedQuantity) {this.checkedQuantity.setValue(checkedQuantity);}

    public final ReadOnlyStringProperty checkedQuantityStringProperty() {return checkedQuantityString;}
    public final String getCheckedQuantityString() {return checkedQuantityString.getValue() ;}

    // immutable properties
    public final CartSet getSourceCartSet() {return sourceCartSet;}
    public final Integer getSourceQuantity() {return sourceCartSet.getQuantity();}
    public final String getTypeString() {return sourceCartSet.getTypeString();}
    public final CartStatus getStatus() {return sourceCartSet.getStatus();}
    public final String getStatusDesc() {return sourceCartSet.getStatusDesc();}
    public final String getPlaceString() {return sourceCartSet.getPlaceString();}

}
