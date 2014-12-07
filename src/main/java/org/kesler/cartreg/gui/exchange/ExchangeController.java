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
    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        checkDirect();
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
    protected void handleInCartSetsTableViewMouseClick(MouseEvent ev) {
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
        log.info("Add In CartSet");
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
                log.info("Adding In CartSet: "
                        + moveCartSet.getModel()
                        + " (" + moveCartSet.getStatusDesc() + ") - "
                        + moveCartSet.getQuantity() + " complete");
            }
        }
    }

    private void editInCartSet() {
        log.info("Edit In CartSet");
        CartSet selectedMoveCartSet = inCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            Integer initialSourceQuantity = selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity();
            quantityController.showAndWait(stage,selectedMoveCartSet.getQuantity(),initialSourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                selectedMoveCartSet.setQuantity(quantityController.getQuantity());
                selectedSourceCartSet.setQuantity(initialSourceQuantity-quantityController.getQuantity());
                FXUtils.updateObservableList(observableInCartSets);
                log.info("Editing In CartSet: "
                        + selectedMoveCartSet.getModel()
                        + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                        + selectedMoveCartSet.getQuantity() + " complete");
            }
        }
    }

    private void removeInCartSet() {
        log.info("Remove In CartSet");
        CartSet selectedMoveCartSet = inCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            selectedSourceCartSet.setQuantity(selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity());
            toFromCartSets.remove(selectedMoveCartSet);
            observableInCartSets.removeAll(selectedMoveCartSet);
            log.info("Removing In CartSet: "
                    + selectedMoveCartSet.getModel()
                    + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                    + selectedMoveCartSet.getQuantity() + " complete");

        }
    }

    private void addOutCartSet() {
        log.info("Add Out CartSet");
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
                log.info("Adding Out CartSet: "
                        + moveCartSet.getModel()
                        + " (" + moveCartSet.getStatusDesc() + ") - "
                        + moveCartSet.getQuantity() + " complete");
            }
        }
    }

    private void editOutCartSet() {
        log.info("Edit Out CartSet");
        CartSet selectedMoveCartSet = outCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            Integer initialSourceQuantity = selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity();
            quantityController.showAndWait(stage,selectedMoveCartSet.getQuantity(),initialSourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                selectedMoveCartSet.setQuantity(quantityController.getQuantity());
                selectedSourceCartSet.setQuantity(initialSourceQuantity-quantityController.getQuantity());
                FXUtils.updateObservableList(observableOutCartSets);
                log.info("Editing Out CartSet: "
                        + selectedMoveCartSet.getModel()
                        + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                        + selectedMoveCartSet.getQuantity() + " complete");
            }
        }
    }

    private void removeOutCartSet() {
        log.info("Remove Out CartSet");
        CartSet selectedMoveCartSet = outCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            selectedSourceCartSet.setQuantity(selectedSourceCartSet.getQuantity()+selectedMoveCartSet.getQuantity());
            toFromCartSets.remove(selectedMoveCartSet);
            observableOutCartSets.removeAll(selectedMoveCartSet);
            log.info("Removing Out CartSet: "
                    + selectedMoveCartSet.getModel()
                    + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                    + selectedMoveCartSet.getQuantity() + " complete");
        }
    }




    @Override
    protected void handleOk() {
        log.info("Handle OK action");
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
        }
    }

    @Override
    protected void handleCancel() {

        log.info("Handle Cancel action");
        Set<CartSet> moveCartSets = toFromCartSets.keySet();
        for (CartSet moveCartSet:moveCartSets) {
            CartSet sourceCartSet = toFromCartSets.get(moveCartSet);
            Integer moveQuantity = moveCartSet.getQuantity();
            Integer sourceQuantity = sourceCartSet.getQuantity();

            sourceCartSet.setQuantity(sourceQuantity + moveQuantity);
        }
        clearLists();
        hideStage();

    }


    private void checkDirect() {
        log.info("Checking direct...");
        /// Проверяем дирекцию в отдельном потоке
        DirectCheckTask directCheckTask = new DirectCheckTask();
        BooleanBinding runningBinding = directCheckTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(directCheckTask).start();

    }


    private void saveCartSets() {
        log.info("Saving exchange...");
        /// Сохраняем все в отдельном потоке
        SavingTask savingTask = new SavingTask();
        BooleanBinding runningBinding = savingTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(savingTask).start();


    }


    private void clearLists() {
        toFromCartSets.clear();
        observableInCartSets.clear();
        observableOutCartSets.clear();
    }

    ///// Класс для сохранения в отдельном потоке

    class DirectCheckTask extends Task<Collection<Place>> {
        @Override
        protected Collection<Place> call() throws Exception {

            Collection<Place> directs = placeService.getDirects();

            return directs;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            Collection<Place> directs = getValue();
            if (directs.size()==0) {
                Dialogs.create()
                        .owner(stage)
                        .title("Внимание")
                        .message("Дирекция не определена, добавьте дирекцию")
                        .showWarning();
                Place.Type[] placeTypes = {Place.Type.DIRECT};
                placeListController.showAndWaitSelect(stage, placeTypes);
                if (placeListController.getResult()==Result.OK) {
                    direct = placeListController.getSelectedItem();
                } else {
                    hideStage();
                }
            } else if (directs.size()>1) {
                Dialogs.create()
                        .owner(stage)
                        .title("Внимание")
                        .message("Дирекция не определена, выберите дирекцию")
                        .showWarning();
                Place.Type[] placeTypes = {Place.Type.DIRECT};
                placeListController.showAndWaitSelect(stage, placeTypes);
                if (placeListController.getResult()==Result.OK) {
                    direct = placeListController.getSelectedItem();
                } else {
                    hideStage();
                }
            } else {
                direct = directs.iterator().next();
            }
            log.info("Selecting direct successfull");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Reading error: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при чтении из базы данных: " + exception)
                    .showException(exception);
        }


    }


    class SavingTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        protected Void call() throws Exception {

            for (CartSet moveCartSet:observableInCartSets) {

                log.info("Adding In move CartSet: "
                        + moveCartSet.getModel()
                        + " (" + moveCartSet.getStatusDesc() + ") - "
                        + moveCartSet.getQuantity());
                cartSetService.addCartSet(moveCartSet);
                log.info("Adding In move CartSet complete");

                CartSet sourceCartSet = toFromCartSets.get(moveCartSet);

                log.info("Updating In source CartSet: "
                        + sourceCartSet.getModel()
                        + " (" + sourceCartSet.getStatusDesc() + ") - "
                        + sourceCartSet.getQuantity());
                cartSetService.updateCartSet(sourceCartSet);
                log.info("Updating In move CartSet complete");

                saveCartSetChange(sourceCartSet, moveCartSet, CartSetChange.Type.RECIEVE);
            }


            // сохраняем отправленные наборы
            for (CartSet moveCartSet:observableOutCartSets) {

                log.info("Adding Out move CartSet: "
                        + moveCartSet.getModel()
                        + " (" + moveCartSet.getStatusDesc() + ") - "
                        + moveCartSet.getQuantity());
                cartSetService.addCartSet(moveCartSet);
                log.info("Adding Out move CartSet complete");

                CartSet sourceCartSet = toFromCartSets.get(moveCartSet);

                log.info("Updating Out source CartSet: "
                        + sourceCartSet.getModel()
                        + " (" + sourceCartSet.getStatusDesc() + ") - "
                        + sourceCartSet.getQuantity());
                cartSetService.updateCartSet(sourceCartSet);
                log.info("Updating Out move CartSet complete");

                saveCartSetChange(sourceCartSet, moveCartSet, CartSetChange.Type.SEND);
            }

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Saving complete");
            clearLists();
            Dialogs.create()
                    .owner(stage)
                    .title("Оповещение")
                    .message("Прием/выдача сохранены")
                    .showInformation();

            hideStage();

        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Saving error: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при сохранении: " + exception)
                    .showException(exception);
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


            log.info("Saving change ");
            cartSetChangeService.addChange(cartSetChange);
            log.info("Saving change complete");

        }

    }

}
