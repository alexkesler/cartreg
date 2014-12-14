package org.kesler.cartreg.gui.exchange;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.*;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.cartsetreestr.CartSetComparator;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.gui.placecartsetsselect.PlaceCartSetsSelectController;
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
    private PlaceCartSetsSelectController placeCartSetsSelectController;

    @Autowired
    private QuantityController quantityController;


    private Place branch;
    private Place direct;

    private final ObservableList<CartSet> observableInCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableOutCartSets = FXCollections.observableArrayList();

    private Map<CartSet,CartSet> toFromInCartSets = new HashMap<CartSet, CartSet>();
    private Map<CartSet,CartSet> toFromOutCartSets = new HashMap<CartSet, CartSet>();

    @FXML
    protected void initialize() {
        SortedList<CartSet> sortedInCartSets = new SortedList<CartSet>(observableInCartSets, new CartSetComparator());
        inCartSetsTableView.setItems(sortedInCartSets);
        SortedList<CartSet> sortedOutCartSets = new SortedList<CartSet>(observableOutCartSets, new CartSetComparator());
        outCartSetsTableView.setItems(sortedOutCartSets);
    }

    public void show(Window owner, Place place) {
        this.branch = place;
        checkDirect();
        clearLists();
        Image icon = new Image(this.getClass().getResourceAsStream("/images/exchange.png"));

        super.show(owner, "Прием/выдача", icon);
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
        FXUtils.triggerUpdateTableView(inCartSetsTableView);
        FXUtils.triggerUpdateTableView(outCartSetsTableView);
    }

    private void addInCartSet() {
        log.info("Add In CartSet");
        placeCartSetsSelectController.showAndWait(stage, branch, toFromInCartSets);
        if (placeCartSetsSelectController.getResult()==Result.OK) {
            toFromInCartSets.clear();
            toFromInCartSets.putAll(placeCartSetsSelectController.getSelectedToSourceCartSetsMap());
            /// Назначаем прибывшим место размещения и статус
            for (CartSet cartSet:toFromInCartSets.keySet()) {
                cartSet.setPlace(direct);
                cartSet.setStatus(CartStatus.EMPTY);
            }
            observableInCartSets.clear();
            observableInCartSets.addAll(toFromInCartSets.keySet());

            log.info("Adding In CartSets complete");
        }
        log.info("Adding In CartSets canceled");
    }

    private void editInCartSet() {
        log.info("Edit In CartSet");
        CartSet selectedMoveCartSet = inCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromInCartSets.get(selectedMoveCartSet);
            Integer sourceQuantity = selectedSourceCartSet.getQuantity();
            quantityController.showAndWait(stage,selectedMoveCartSet.getQuantity(),sourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                selectedMoveCartSet.setQuantity(quantityController.getQuantity());
                updateContent();
                log.info("Editing In CartSet: "
                        + selectedMoveCartSet.getTypeString()
                        + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                        + selectedMoveCartSet.getQuantity() + " complete");
            }
        }
    }

    private void removeInCartSet() {
        log.info("Remove In CartSet");
        CartSet selectedMoveCartSet = inCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            toFromInCartSets.remove(selectedMoveCartSet);
            observableInCartSets.removeAll(selectedMoveCartSet);
            log.info("Removing In CartSet: "
                    + selectedMoveCartSet.getTypeString()
                    + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                    + selectedMoveCartSet.getQuantity() + " complete");

        }
    }

    private void addOutCartSet() {
        log.info("Add Out CartSet");
        CartStatus[] statuses = {CartStatus.NEW,CartStatus.FILLED};
        placeCartSetsSelectController.showAndWait(stage, direct,statuses,toFromOutCartSets);
        if (placeCartSetsSelectController.getResult()==Result.OK) {
            toFromOutCartSets.clear();
            toFromOutCartSets.putAll(placeCartSetsSelectController.getSelectedToSourceCartSetsMap());
            for (CartSet cartSet : toFromOutCartSets.keySet()) {
                cartSet.setPlace(branch);
                cartSet.setStatus(CartStatus.INSTALLED);
            }
            observableOutCartSets.clear();
            observableOutCartSets.addAll(toFromOutCartSets.keySet());
            log.info("Adding Out CartSets complete ");
        }
        log.info("Adding Out CartSets canceled ");

    }

    private void editOutCartSet() {
        log.info("Edit Out CartSet");
        CartSet selectedMoveCartSet = outCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet selectedSourceCartSet = toFromOutCartSets.get(selectedMoveCartSet);
            Integer sourceQuantity = selectedSourceCartSet.getQuantity();
            quantityController.showAndWait(stage, selectedMoveCartSet.getQuantity(),sourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                selectedMoveCartSet.setQuantity(quantityController.getQuantity());
                updateContent();
                log.info("Editing Out CartSet: "
                        + selectedMoveCartSet.getTypeString()
                        + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                        + selectedMoveCartSet.getQuantity() + " complete");
            }
        }
    }

    private void removeOutCartSet() {
        log.info("Remove Out CartSet");
        CartSet selectedMoveCartSet = outCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            toFromOutCartSets.remove(selectedMoveCartSet);
            observableOutCartSets.removeAll(selectedMoveCartSet);
            log.info("Removing Out CartSet: "
                    + selectedMoveCartSet.getTypeString()
                    + " (" + selectedMoveCartSet.getStatusDesc() + ") - "
                    + selectedMoveCartSet.getQuantity() + " complete");
        }
    }




    @Override
    protected void handleOk() {
        log.info("Handle OK action");
        String message = "Переместить картриджи?";
        for (CartSet cartSet: observableInCartSets) {
            message += "\n" + cartSet.getTypeString() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity()
            + " - > " + direct.getName();
        }
        for (CartSet cartSet: observableOutCartSets) {
            message += "\n" + cartSet.getTypeString() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity()
            + " - > " + branch.getName();
        }


        Action response = Dialogs.create()
                .owner(stage)
                .title("Подтверждение")
                .message(message)
                .actions(Dialog.ACTION_YES, Dialog.ACTION_CANCEL)
                .showConfirm();
        if (response== Dialog.ACTION_YES) {
            saveCartSets();
        }
    }

    @Override
    protected void handleCancel() {

        log.info("Handle Cancel action");
        clearLists();
        hideStage();

    }


    private void checkDirect() {
        log.info("Checking direct...");
        /// Проверяем дирекцию в отдельном потоке
        CheckDirectTask checkDirectTask = new CheckDirectTask();
        BooleanBinding runningBinding = checkDirectTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(checkDirectTask).start();

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
        toFromInCartSets.clear();
        toFromOutCartSets.clear();
        observableInCartSets.clear();
        observableOutCartSets.clear();
    }

    ///// Класс для сохранения в отдельном потоке

    class CheckDirectTask extends Task<Collection<Place>> {
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
            log.info("Selecting direct successful");
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
            hideStage();
        }

    }


    class SavingTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        protected Void call() throws Exception {

            for (Map.Entry<CartSet,CartSet> inCartSetEntry:toFromInCartSets.entrySet()) {

                CartSet moveCartSet = inCartSetEntry.getKey();
                log.info("Adding In move CartSet: "
                        + moveCartSet.getTypeString()
                        + " (" + moveCartSet.getStatusDesc() + ") - "
                        + moveCartSet.getQuantity());
                cartSetService.addCartSet(moveCartSet);
                log.info("Adding In move CartSet complete");

                CartSet sourceCartSet = inCartSetEntry.getValue();

                // обновляем количество
                sourceCartSet.setQuantity(sourceCartSet.getQuantity()-moveCartSet.getQuantity());

                log.info("Updating In source CartSet: "
                        + sourceCartSet.getTypeString()
                        + " (" + sourceCartSet.getStatusDesc() + ") - "
                        + sourceCartSet.getQuantity());
                cartSetService.updateCartSet(sourceCartSet);
                log.info("Updating In move CartSet complete");

                saveCartSetChange(sourceCartSet, moveCartSet, CartSetChange.Type.RECIEVE);
            }


            // сохраняем отправленные наборы
            for (Map.Entry<CartSet,CartSet> inCartSetEntry:toFromOutCartSets.entrySet()) {

                CartSet moveCartSet = inCartSetEntry.getKey();
                log.info("Adding Out move CartSet: "
                        + moveCartSet.getTypeString()
                        + " (" + moveCartSet.getStatusDesc() + ") - "
                        + moveCartSet.getQuantity());
                cartSetService.addCartSet(moveCartSet);
                log.info("Adding Out move CartSet complete");

                CartSet sourceCartSet = inCartSetEntry.getValue();

                // обновляем количество
                sourceCartSet.setQuantity(sourceCartSet.getQuantity()-moveCartSet.getQuantity());

                log.info("Updating Out source CartSet: "
                        + sourceCartSet.getTypeString()
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
