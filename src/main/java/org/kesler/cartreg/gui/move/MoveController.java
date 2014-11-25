package org.kesler.cartreg.gui.move;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.*;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.gui.util.QuantityController;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.MoveService;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.util.FXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Контроллер для перемещений
 */
@Component
public class MoveController extends AbstractController {

    @FXML protected Label placeLabel;
    @FXML protected TableView<CartSet> inCartSetsTableView;
    @FXML protected TableView<CartSet> outCartSetsTableView;

    @Autowired
    private MoveService moveService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private CartSetService cartSetService;

    @Autowired
    private PlaceListController placeListController;

    @Autowired
    private PlaceCartSetsController placeCartSetsController;

    @Autowired
    private QuantityController quantityController;


    private Place branch;
    private Place direct;

    private final ObservableList<CartSet> observableInCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableOutCartSets = FXCollections.observableArrayList();

    private Map<CartSet,CartSet> toFromCartSets = new HashMap<CartSet, CartSet>();

    @FXML
    protected void initialize() {
        inCartSetsTableView.setItems(observableInCartSets);
        outCartSetsTableView.setItems(observableOutCartSets);
    }

    public void show(Window owner, Place place) {
        this.branch = place;
        Collection<Place> directs = placeService.getDirects();
        if (directs.size()==0) {
            Dialogs.create()
                    .owner(owner)
                    .title("Внимание")
                    .message("Дирекция не определена, добавьте дирекцию")
                    .showWarning();
            PlaceType[] placeTypes = {PlaceType.DIRECT};
            placeListController.showAndWaitSelect(owner, placeTypes);
            if (placeListController.getResult()==Result.OK) {
                direct = placeListController.getSelectedItem();
            } else {
                return;
            }
        } else if (directs.size()>1) {
            Dialogs.create()
                    .owner(owner)
                    .title("Внимание")
                    .message("Дирекция не определена, выберите дирекцию")
                    .showWarning();
            PlaceType[] placeTypes = {PlaceType.DIRECT};
            placeListController.showAndWaitSelect(owner, placeTypes);
            if (placeListController.getResult()==Result.OK) {
                direct = placeListController.getSelectedItem();
            } else {
                return;
            }
        } else {
            direct = directs.iterator().next();
        }

        observableInCartSets.clear();
        observableOutCartSets.clear();
        toFromCartSets.clear();
        super.show(owner, "Прием/выдача");
    }

    @FXML
    protected void handleAddInCartSetButtonAction(ActionEvent ev) {
        addInCartSet();
    }

    @FXML
    protected void handleiInCartSetsTableViewMouceClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editInCartSet();
        }
    }

    @FXML
    protected void handleEditInCartSetButtonAction(ActionEvent ev) {
        editInCartSet();
    }

    @FXML
    protected void handleRemoveInCartSetButtonAction(ActionEvent ev) {
        removeInCartSet();
    }

    @FXML
    protected void handleAddOutCartSetButtonAction(ActionEvent ev) {
        addOutCartSet();
    }

    @FXML
    protected void handleEditOutCartSetButtonAction(ActionEvent ev) {
        editOutCartSet();
    }

    @FXML
    protected void handleOutCartSetsTableViewMouceClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editOutCartSet();
        }
    }

    @FXML
    protected void handleRemoveOutCartSetButtonAction(ActionEvent ev) {
        removeOutCartSet();
    }

    @Override
    protected void updateContent() {
        placeLabel.setText(branch ==null?"Не определено": branch.getCommonName());
    }

    private void addInCartSet() {
        placeCartSetsController.showAndWaitSelect(stage, branch);
        if (placeCartSetsController.getResult()==Result.OK) {
            CartSet sourceCartSet = placeCartSetsController.getSelectedItem();
            CartSet moveCartSet = sourceCartSet.copyCartSet();
            Integer moveQuantity = 4;
            Integer sourceQuantity = sourceCartSet.getQuantity();
            quantityController.showAndWait(stage, moveQuantity, sourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                moveQuantity = quantityController.getQuantity();
                moveCartSet.setQuantity(moveQuantity);
                moveCartSet.setStatus(CartStatus.EMPTY);

                sourceCartSet.setQuantity(sourceQuantity-moveQuantity);
                // запоминаем куда и откуда перемещаем
                toFromCartSets.put(moveCartSet, sourceCartSet);
                observableInCartSets.addAll(moveCartSet);
            }
        }
    }

    private void editInCartSet() {
        CartSet selectedMoveCartSet = inCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            Integer initialSourceQuantity = selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity();
            quantityController.showAndWait(stage,selectedMoveCartSet.getQuantity(),initialSourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                selectedMoveCartSet.setQuantity(quantityController.getQuantity());
                selectedSourceCartSet.setQuantity(initialSourceQuantity-quantityController.getQuantity());
                FXUtils.updateObservableList(observableInCartSets);
            }
        }
    }

    private void removeInCartSet() {
        CartSet selectedMoveCartSet = inCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            selectedSourceCartSet.setQuantity(selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity());
            toFromCartSets.remove(selectedMoveCartSet);
            observableInCartSets.removeAll(selectedMoveCartSet);
        }
    }

    private void addOutCartSet() {
        CartStatus[] statuses = {CartStatus.NEW,CartStatus.FILED};
        placeCartSetsController.showAndWaitSelect(stage, direct,statuses);
        if (placeCartSetsController.getResult()==Result.OK) {
            CartSet sourceCartSet = placeCartSetsController.getSelectedItem();
            CartSet moveCartSet = sourceCartSet.copyCartSet();
            Integer moveQuantity = 4;
            Integer sourceQuantity = sourceCartSet.getQuantity();
            quantityController.showAndWait(stage, moveQuantity, sourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                moveQuantity = quantityController.getQuantity();
                moveCartSet.setQuantity(moveQuantity);
                sourceCartSet.setQuantity(sourceQuantity-moveQuantity);

                toFromCartSets.put(moveCartSet, sourceCartSet);
                observableOutCartSets.addAll(moveCartSet);
            }
        }
    }

    private void editOutCartSet() {
        CartSet selectedMoveCartSet = outCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            Integer initialSourceQuantity = selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity();
            quantityController.showAndWait(stage,selectedMoveCartSet.getQuantity(),initialSourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                selectedMoveCartSet.setQuantity(quantityController.getQuantity());
                selectedSourceCartSet.setQuantity(initialSourceQuantity-quantityController.getQuantity());
                FXUtils.updateObservableList(observableOutCartSets);
            }
        }
    }

    private void removeOutCartSet() {
        CartSet selectedMoveCartSet = outCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            selectedSourceCartSet.setQuantity(selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity());
            toFromCartSets.remove(selectedMoveCartSet);
            observableOutCartSets.removeAll(selectedMoveCartSet);
        }
    }




    @Override
    protected void handleOk() {
        String message = "Переместить картриджи?";
        Action response = Dialogs.create()
                .owner(stage)
                .title("Подтверждение")
                .message(message)
                .actions(Dialog.ACTION_YES,Dialog.ACTION_CANCEL)
                .showConfirm();
        if (response== Dialog.ACTION_YES) {
            saveMoves();
            saveCartSets();
            clearLists();
            stage.hide();
        }
    }

    @Override
    protected void handleCancel() {
        Set<CartSet> moveCartSets = toFromCartSets.keySet();
        for (CartSet moveCartSet:moveCartSets) {
            CartSet sourceCartSet = toFromCartSets.get(moveCartSet);
            Integer moveQuantity = moveCartSet.getQuantity();
            Integer sourceQuantity = sourceCartSet.getQuantity();

            sourceCartSet.setQuantity(sourceQuantity + moveQuantity);
        }
        clearLists();
        stage.hide();
    }


    private void saveMoves() {
        for (CartSet cartSet:observableInCartSets) {
            Move move = new Move();
            move.setFromPlace(branch);
            move.setToPlace(direct);
            move.setMoveDate(new Date());
            move.setCartType(cartSet.getType());
            move.setCartStatus(cartSet.getStatus());
            move.setQuantity(cartSet.getQuantity());

            moveService.addMove(move);
        }

        for (CartSet cartSet:observableOutCartSets) {
            Move move = new Move();
            move.setFromPlace(direct);
            move.setToPlace(branch);
            move.setMoveDate(new Date());
            move.setCartType(cartSet.getType());
            move.setCartStatus(cartSet.getStatus());
            move.setQuantity(cartSet.getQuantity());

            moveService.addMove(move);
        }

    }

    private void saveCartSets() {
        // сохраняем поступившие наборы
        for (CartSet moveCartSet:observableInCartSets) {
            moveCartSet.setPlace(direct);
            cartSetService.addCartSet(moveCartSet);
            cartSetService.updateCartSet(toFromCartSets.get(moveCartSet));
        }
        // сохраняем отправленные наборы
        for (CartSet moveCartSet:observableOutCartSets) {
            moveCartSet.setStatus(CartStatus.INSTALLED); // статус -> установлен
            moveCartSet.setPlace(branch);
            cartSetService.addCartSet(moveCartSet);
            cartSetService.updateCartSet(toFromCartSets.get(moveCartSet));
        }

    }

    private void clearLists() {
        toFromCartSets.clear();
        observableInCartSets.clear();
        observableOutCartSets.clear();
    }
}
