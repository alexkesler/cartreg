package org.kesler.cartreg.gui.place;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.Callback;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.domain.PlaceType;
import org.kesler.cartreg.gui.AbstractController;
import org.springframework.stereotype.Component;

@Component
public class PlaceController extends AbstractController {

    private Place place;

    private PlaceType[] placeTypes;

    @FXML protected TextField nameTextField;
    @FXML protected ComboBox<PlaceType> placeTypeComboBox;

    @FXML void initialize() {
        placeTypeComboBox.setCellFactory(new Callback<ListView<PlaceType>, ListCell<PlaceType>>() {
            @Override
            public ListCell<PlaceType> call(ListView<PlaceType> param) {
                return new ListCell<PlaceType>() {
                    @Override
                    protected void updateItem(PlaceType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item==null || empty) {
                            setText(null);
                        } else {
                            setText(item.getDesc());
                        }
                    }
                };
            }
        });
        placeTypeComboBox.setButtonCell(new ListCell<PlaceType>(){
            @Override
            protected void updateItem(PlaceType item, boolean empty) {
                super.updateItem(item, empty);
                if (item==null || empty) {
                    setText(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });
    }

    public void showAndWait(Window owner, Place place) {
        this.place = place;
        placeTypes = null;
        super.showAndWait(owner,"Размещение");
    }

    public void showAndWait(Window owner, Place place, PlaceType[] placeTypes) {
        this.place = place;
        this.placeTypes = placeTypes;
        super.showAndWait(owner,"Размещение");
    }

    @Override
    protected void updateContent() {
        placeTypeComboBox.getItems().clear();
        if (placeTypes!=null) {
            placeTypeComboBox.getItems().addAll(placeTypes);
        } else {
            placeTypeComboBox.getItems().addAll(PlaceType.values());
        }

//        placeTypeComboBox.getSelectionModel().select(place.getType());
        placeTypeComboBox.setValue(place.getType());
        nameTextField.setText(place.getName());
        nameTextField.requestFocus();
        nameTextField.selectAll();
    }

    @Override
    protected void updateResult() {
        place.setType(placeTypeComboBox.getValue());
        place.setName(nameTextField.getText());
    }

}
