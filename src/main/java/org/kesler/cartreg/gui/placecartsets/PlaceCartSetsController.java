package org.kesler.cartreg.gui.placecartsets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.kesler.cartreg.domain.*;
import org.kesler.cartreg.gui.AbsractListController;
import org.kesler.cartreg.gui.cartset.CartSetController;
import org.kesler.cartreg.service.CartSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Контроллер окна наличия картриджей
 */
@Component
public class PlaceCartSetsController extends AbsractListController<CartSet> {

    @FXML protected Label placeLabel;
    @FXML protected TableView<CartSet> cartSetsTableView;


    @Autowired
    protected CartSetController cartSetController;

    @Autowired
    protected CartSetService cartSetService;

    private Place place;
    private CartStatus[] statuses;

    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();

    @FXML protected void initialize() {
        cartSetsTableView.setItems(observableCartSets);
    }

    public void show(Window owner, Place place) {
        this.place = place;
        statuses = null;
        super.show(owner, "Наличие картриджей");
    }

    public void showAndWaitSelect(Window owner, Place place) {
        this.place = place;
        statuses = null;
        super.showAndWaitSelect(owner, "Наличие картриджей");
    }

    public void showAndWaitSelect(Window owner, Place place, CartStatus[] statuses) {
        this.place = place;
        this.statuses = statuses;
        super.showAndWaitSelect(owner, "Наличие картриджей");
    }

    @FXML protected void handleAddButtonAction(ActionEvent ev) {
        addCartSet();
    }

    @FXML protected void handleEditButtonAction(ActionEvent ev) {
        editCartSet();
    }

    @FXML protected void handleCartSetsTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            if (select) {
                handleOk();
            } else {
                editCartSet();
            }
        }
    }

    @FXML protected void handleRemoveButtonAction(ActionEvent ev) {
        removeCartSet();
    }

    @Override
    protected void updateContent() {
        placeLabel.setText(place==null?"Не опеределено":place.getCommonName());
        observableCartSets.clear();

        Collection<CartSet> cartSets = cartSetService.getCartSetsByPlace(place);


        if (statuses!=null) {
            Iterator<CartSet> cartSetIterator = cartSets.iterator();
            CartSet cartSet;
            while (cartSetIterator.hasNext()) {
                cartSet = cartSetIterator.next();
                boolean fit = false;
                for (CartStatus status:statuses) {
                    if (status.equals(cartSet.getStatus())) {
                        fit=true;
                        break;
                    }
                }
                if (!fit) cartSetIterator.remove();
            }
        }

        observableCartSets.addAll(cartSets);
    }

    @Override
    protected void updateResult() {
        selectedItem = cartSetsTableView.getSelectionModel().getSelectedItem();
    }

    private void addCartSet() {
        CartSet cartSet = new CartSet();
        cartSet.setPlace(place);
        // определяем тип по умолчанию в зависимости от расположения
        switch (place.getType()) {
            case STORAGE:
                cartSet.setStatus(CartStatus.NEW);
                break;
            case DIRECT:
                cartSet.setStatus(CartStatus.FILED);
                break;
            case BRANCH:
                cartSet.setStatus(CartStatus.INSTALLED);
                break;
            default:
                cartSet.setStatus(CartStatus.NONE);
        }

        cartSetController.showAndWait(stage, cartSet);
        if (cartSetController.getResult()==Result.OK) {
            cartSetService.addCartSet(cartSet);
            updateContent();
        }
    }

    private void editCartSet() {
        CartSet selectedCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            cartSetController.showAndWait(stage,selectedCartSet);
            if (cartSetController.getResult()==Result.OK) {
                cartSetService.updateCartSet(selectedCartSet);
                updateContent();
            }

        }
    }

    private void removeCartSet() {
        CartSet selectedCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            cartSetService.removeCartSet(selectedCartSet);
            updateContent();
        }
    }

}
