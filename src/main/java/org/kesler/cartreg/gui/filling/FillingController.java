package org.kesler.cartreg.gui.filling;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.domain.PlaceType;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Контроллер для окна заправки
 */
@Component
public class FillingController extends AbstractController {

    @FXML protected TableView<CartSet> emptyCartSetsTableView;
    @FXML protected TableView<CartSet> filedCartSetsTableView;
    @FXML protected TableView<CartSet>  defectCartSetsTableView;

    @Autowired
    protected PlaceService placeService;

    @Autowired
    protected CartSetService cartSetService;

    @Autowired
    protected PlaceListController placeListController;

    @Autowired
    protected PlaceCartSetsController placeCartSetsController;

    private final ObservableList<CartSet> observableEmptyCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableFiledCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableDefectCartSet = FXCollections.observableArrayList();

    private Place direct;

    private final Map<CartSet,CartSet> emptyToFilledCartSets = new HashMap<CartSet, CartSet>();
    private final Map<CartSet, CartSet> emptyToDefectCartSet = new HashMap<CartSet, CartSet>();

    @FXML
    protected void initialize() {
        emptyCartSetsTableView.setItems(observableEmptyCartSets);
        filedCartSetsTableView.setItems(observableFiledCartSets);
        defectCartSetsTableView.setItems(observableDefectCartSet);
    }

    @Override
    public void show(Window owner) {
        if (!checkDirect(owner)) return;

        clearLists();
        addAllEmptyFromDirect();

        super.show(owner,"Заправка картриджей");
    }

    private boolean checkDirect(Window owner) {
        Collection<Place> directs = placeService.getDirects();
        if (directs.size()==0) {
            Dialogs.create()
                    .owner(owner)
                    .title("Внимание")
                    .message("Дирекция не определена, добавьте дирекцию")
                    .showWarning();
            PlaceType[] placeTypes = {PlaceType.DIRECT};
            placeListController.showAndWaitSelect(owner, placeTypes);
            if (placeListController.getResult()== Result.OK) {
                direct = placeListController.getSelectedItem();
            } else {
                return false;
            }
        } else if (directs.size()>1) {
            Dialogs.create()
                    .owner(owner)
                    .title("Внимание")
                    .message("Дирекция не определена, выберите дирекцию")
                    .showWarning();
            PlaceType[] placeTypes = {PlaceType.DIRECT};
            placeListController.showAndWaitSelect(owner, placeTypes);
            if (placeListController.getResult()== Result.OK) {
                direct = placeListController.getSelectedItem();
            } else {
                return false;
            }
        } else {
            direct = directs.iterator().next();
        }
        return true;
    }

    // Управление Панелью "На заправку"

    @FXML
    protected void handleAddEmptyCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleEditEmptyCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleEmptyCartSetsTableViewMouseClick(MouseEvent ev) {

    }

    @FXML
    protected void handleRemoveEmptyCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleFillMenuItemAction(ActionEvent ev) {

    }

    @FXML
    protected void handleDefectMenuItemAction(ActionEvent ev) {

    }

    // Управление панелью "Заправленные"

    @FXML
    protected void handleEditFiledCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleFiledCartSetsTableViewMouseClick(MouseEvent ev) {

    }

    @FXML
    protected void handleRemoveFiledCartSetButtonAction(ActionEvent ev) {

    }


    // Управление панелью "Неисправные"

    @FXML
    protected void handleEditDefectCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleDefectCartSetsTableViewMouseClick(MouseEvent ev) {

    }

    @FXML
    protected void handleRemoveDefectCartSetButtonAction(ActionEvent ev) {

    }


    private void addAllEmptyFromDirect() {
        Collection<CartSet> cartSets = cartSetService.getCartSetsByPlace(direct);
        Iterator<CartSet> cartSetIterator = cartSets.iterator();
        while (cartSetIterator.hasNext()) {
            CartSet cartSet = cartSetIterator.next();
            if (cartSet.getStatus() != CartStatus.EMPTY) cartSetIterator.remove();
        }

        observableEmptyCartSets.clear();
        observableEmptyCartSets.addAll(cartSets);
    }



    private void clearLists() {
        observableEmptyCartSets.clear();
        observableFiledCartSets.clear();
        observableDefectCartSet.clear();

        emptyToFilledCartSets.clear();
        emptyToDefectCartSet.clear();
    }
}
