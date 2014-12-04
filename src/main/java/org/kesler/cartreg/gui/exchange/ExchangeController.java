package org.kesler.cartreg.gui.exchange;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.util.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Контроллер для перемещений
 */
@Component
public class ExchangeController extends AbstractController {

    @FXML protected Label placeLabel;
    @FXML protected TableView<CartSet> inCartSetsTableView;
    @FXML protected TableView<CartSet> outCartSetsTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;

    @Autowired
    private CartSetChangeService cartSetChangeService;

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
            Place.Type[] placeTypes = {Place.Type.DIRECT};
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
            Place.Type[] placeTypes = {Place.Type.DIRECT};
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
                moveCartSet.setPlace(direct);
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
        CartStatus[] statuses = {CartStatus.NEW,CartStatus.FILLED};
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
                moveCartSet.setPlace(branch);
                moveCartSet.setStatus(CartStatus.INSTALLED);
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
        for (CartSet cartSet: observableInCartSets) {
            message += "\n" + cartSet.getModel() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity()
            + " - > " + direct.getName();
        }
        for (CartSet cartSet: observableOutCartSets) {
            message += "\n" + cartSet.getModel() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity()
            + " - > " + branch.getName();
        }


        Action response = Dialogs.create()
                .owner(stage)
                .title("Подтверждение")
                .message(message)
                .actions(Dialog.ACTION_YES,Dialog.ACTION_CANCEL)
                .showConfirm();
        if (response== Dialog.ACTION_YES) {
            saveCartSets();
            clearLists();
            Dialogs.create()
                    .owner(stage)
                    .title("Оповещение")
                    .message("Перемещено")
                    .showInformation();
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

    private void saveCartSets() {

        // сохраняем поступившие наборы
        for (CartSet moveCartSet:observableInCartSets) {

            log.info("Adding move CartSet...");
            AddCartSetTask addCartSetTask = new AddCartSetTask(moveCartSet);
            BooleanBinding runningBinding = addCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addCartSetTask).start();


            CartSet sourceCartSet = toFromCartSets.get(moveCartSet);

            log.info("Updating source CartSet...");
            UpdateCartSetTask updateCartSetTask = new UpdateCartSetTask(sourceCartSet);
            runningBinding = updateCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(updateCartSetTask).start();

            saveCartSetChange(sourceCartSet,moveCartSet, CartSetChange.Type.RECIEVE);
        }
        // сохраняем отправленные наборы
        for (CartSet moveCartSet:observableOutCartSets) {

            log.info("Adding move CartSet...");
            AddCartSetTask addCartSetTask = new AddCartSetTask(moveCartSet);
            BooleanBinding runningBinding = addCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addCartSetTask).start();

            CartSet sourceCartSet = toFromCartSets.get(moveCartSet);

            log.info("Updating source CartSet...");
            UpdateCartSetTask updateCartSetTask = new UpdateCartSetTask(sourceCartSet);
            runningBinding = updateCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(updateCartSetTask).start();

            saveCartSetChange(sourceCartSet, moveCartSet, CartSetChange.Type.SEND);
        }

    }

    private void saveCartSetChange(CartSet fromCartSet, CartSet toCartSet, CartSetChange.Type type) {
        CartSetChange cartSetChange = new CartSetChange();
        cartSetChange.setType(type);
        cartSetChange.setFromPlace(fromCartSet.getPlace());
        cartSetChange.setToPlace(toCartSet.getPlace());
        cartSetChange.setCartType(fromCartSet.getType());
        cartSetChange.setFromStatus(fromCartSet.getStatus());
        cartSetChange.setToStatus(toCartSet.getStatus());
        cartSetChange.setQuantity(toCartSet.getQuantity());
        cartSetChange.setChangeDate(new Date());


        log.info("Saving change... ");
        SaveChangeTask saveChangeTask = new SaveChangeTask(cartSetChange);

        BooleanBinding runningBinding = saveChangeTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(saveChangeTask).start();

    }

    private void clearLists() {
        toFromCartSets.clear();
        observableInCartSets.clear();
        observableOutCartSets.clear();
    }

    ///// Классы для сохранения сущностей в отдельном потоке

    class AddCartSetTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartSet cartSet;

        AddCartSetTask(CartSet cartSet) {
            this.cartSet = cartSet;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Adding CartSet...");

            cartSetService.addCartSet(cartSet);

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Adding CartSet complete");
            updateContent();
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error adding CartSet: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при добавлении набора картриджей: " + exception)
                    .showException(exception);
        }
    }

    class UpdateCartSetTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartSet cartSet;

        UpdateCartSetTask(CartSet cartSet) {
            this.cartSet = cartSet;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Updating CartSet...");

            cartSetService.updateCartSet(cartSet);

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Updating CartSet complete");
            updateContent();
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error updating CartSet: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при обновлении набора картриджей: " + exception)
                    .showException(exception);
        }
    }



    // задача для сохранения изменения
    class SaveChangeTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private CartSetChange cartSetChange;

        SaveChangeTask(CartSetChange cartSetChange) {
            this.cartSetChange = cartSetChange;
        }


        @Override
        protected Void call() throws Exception {
            log.debug("Saving change...");
            cartSetChangeService.addChange(cartSetChange);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Saving change complete.");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error saving change: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при сохранении перемещения: " + exception)
                    .showException(exception);
        }
    }




}
