package org.kesler.cartreg.gui.arrival;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.kesler.cartreg.domain.*;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.cartset.CartSetController;
import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.util.FXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * Контроллер для отслеживания поступлений картриджей
 */
@Component
public class ArrivalController extends AbstractController {


    @FXML protected Label placeLabel;
    @FXML protected TableView<CartSet> cartSetTableView;

    @Autowired
    protected PlaceListController placeListController;

    @Autowired
    protected CartSetController cartSetController;

    @Autowired
    protected CartSetService cartSetService;

    @Autowired
    protected CartSetChangeService cartSetChangeService;

    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();
    private Place place;

    @FXML
    protected void initialize() {
        cartSetTableView.setItems(observableCartSets);
    }

    @Override
    public void show(Window owner) {
        observableCartSets.clear();
        super.show(owner, "Поступление картриджей");
    }

    @FXML
    protected void handleSelectPlaceButtonAction(ActionEvent ev) {
        selectPlace();
    }

    @FXML
    protected void handleAddButtonAction(ActionEvent ev) {
        addCartSet();
    }

    @FXML
    protected void handleEditButtonAction(ActionEvent ev) {
        editCartSet();
    }

    @FXML
    protected void handleCartSetTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editCartSet();
        }
    }

    @FXML
    protected void handleRemoveButtonAction(ActionEvent ev) {
        removeCartSet();
    }

    @Override
    protected void updateContent() {
        placeLabel.setText(place==null?"Не определено":place.getCommonName());
        FXUtils.updateObservableList(observableCartSets);
    }

    @Override
    protected void updateResult() {


        for(CartSet cartSet:observableCartSets) {
            cartSetService.addCartSet(cartSet);

            // Сохраняем перемещение
            CartSetChange cartSetChange = new CartSetChange();
            cartSetChange.setCartType(cartSet.getType());
            cartSetChange.setFromPlace(null);
            cartSetChange.setToPlace(cartSet.getPlace());
            cartSetChange.setFromStatus(CartStatus.NONE);
            cartSetChange.setToStatus(cartSet.getStatus());
            cartSetChange.setQuantity(cartSet.getQuantity());
            cartSetChange.setChangeDate(new Date());

            cartSetChangeService.addChange(cartSetChange);
        }
    }

    private void selectPlace() {
        PlaceType[] placeTypes = {PlaceType.DIRECT,PlaceType.STORAGE};
        placeListController.showAndWaitSelect(stage,placeTypes);
        if (placeListController.getResult()==Result.OK) {
            place = placeListController.getSelectedItem();
            updateContent();
        }
    }

    private void addCartSet() {
        CartSet cartSet = new CartSet();
        cartSet.setStatus(CartStatus.NEW);
        cartSet.setQuantity(50);
        cartSet.setPlace(place);
        cartSetController.showAndWait(stage, cartSet);
        if (cartSetController.getResult()==Result.OK) {
            observableCartSets.add(cartSet);
            cartSetTableView.getSelectionModel().select(cartSet);
        }
    }

    private void editCartSet() {
        CartSet selectedCartSet = cartSetTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            cartSetController.showAndWait(stage, selectedCartSet);
            if (cartSetController.getResult()==Result.OK) {
                FXUtils.updateObservableList(observableCartSets);
            }
            cartSetTableView.getSelectionModel().select(selectedCartSet);
        }
    }

    private void removeCartSet() {
        CartSet selectedCartSet = cartSetTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            observableCartSets.remove(selectedCartSet);
        }
    }
}
