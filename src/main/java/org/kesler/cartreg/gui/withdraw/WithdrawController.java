package org.kesler.cartreg.gui.withdraw;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.domain.Place;
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
 * Контроллер для окна списания
 */
@Component
public class WithdrawController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected TableView<CartSet> defectCartSetsTableView;
    @FXML protected TableView<CartSet> withdrawCartSetsTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;

    @Autowired
    protected PlaceService placeService;

    @Autowired
    protected CartSetService cartSetService;

    @Autowired
    protected CartSetChangeService cartSetChangeService;

    @Autowired
    protected PlaceListController placeListController;

    @Autowired
    protected PlaceCartSetsController placeCartSetsController;

    @Autowired
    protected QuantityController quantityController;

    private final ObservableList<CartSet> observableDefectCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableWithdrawCartSets = FXCollections.observableArrayList();


    private Place direct;

    private final Map<CartSet,CartSet> withdrawToDefectCartSets = new HashMap<CartSet, CartSet>();


    @FXML
    protected void initialize() {
        defectCartSetsTableView.setItems(observableDefectCartSets);
        withdrawCartSetsTableView.setItems(observableWithdrawCartSets);
    }

    @Override
    public void show(Window owner) {

        clearLists();

        Image icon = new Image(this.getClass().getResourceAsStream("/images/garbage.png"));

        super.show(owner,"Списание картриджей", icon);
        checkDirect();

    }

    private void checkDirect() {
        log.info("Checking direct...");
        /// Проверяем дирекцию в отдельном потоке
        CheckDirectTask checkDirectTask = new CheckDirectTask();
        BooleanBinding runningBinding = checkDirectTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(checkDirectTask).start();
    }

    // Управление Панелью "Неисправные"

    @FXML
    protected void handleAddDefectCartSetButtonAction(ActionEvent ev) {
        addDefectCartSet();
    }

    @FXML
    protected void handleRemoveDefectCartSetButtonAction(ActionEvent ev) {
        removeDefectCartSet();
    }

    @FXML
    protected void handleWithdrawMenuItemAction(ActionEvent ev) {
        withdrawCartSet();
    }

    @FXML
    protected void handleWithdrawCartSetButtonAction(ActionEvent ev) {
        withdrawCartSet();
    }

    // Управление панелью "Списано"

    @FXML
    protected void handleEditWithdrawCartSetButtonAction(ActionEvent ev) {
        editWithdrawCartSet();
    }

    @FXML
    protected void handleWithdrawCartSetsTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editWithdrawCartSet();
        }
    }

    @FXML
    protected void handleRemoveWithdrawCartSetButtonAction(ActionEvent ev) {
        removeWithdrawCartSet();
    }


    @Override
    protected void updateContent() {
        FXUtils.triggerUpdateTableView(defectCartSetsTableView);
        FXUtils.triggerUpdateTableView(withdrawCartSetsTableView);
    }


    @Override
    protected void handleOk() {
        String message = "Списать картриджи?";
        message += "\n Списано:";
        for (CartSet cartSet: observableWithdrawCartSets) {
            message += "\n" + cartSet.getModel() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity() + " шт";
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

    private void addAllDefectFromDirect() {
        log.info("Getting defect CartSets from direct");
        GettingDefectFromDirectTask gettingDefectFromDirectTask = new GettingDefectFromDirectTask();
        BooleanBinding runningBinding = gettingDefectFromDirectTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(gettingDefectFromDirectTask).start();

    }


    /// Обработчики для панели Неисправные картриджи

    private void addDefectCartSet() {
        CartStatus[] statuses = {CartStatus.DEFECT};
        placeCartSetsController.showAndWaitSelect(stage,direct,statuses);
        if (placeCartSetsController.getResult()==Result.OK) {
            CartSet cartSet = placeCartSetsController.getSelectedItem();
            if (!observableDefectCartSets.contains(cartSet))
                observableDefectCartSets.addAll(cartSet);
        }
    }

    private void removeDefectCartSet() {
        CartSet selectedCartSet = defectCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            observableDefectCartSets.removeAll(selectedCartSet);
            for (Map.Entry<CartSet,CartSet> entry: withdrawToDefectCartSets.entrySet()) {
                if (entry.getValue().equals(selectedCartSet)) {
                    observableWithdrawCartSets.removeAll(entry.getKey());
                    withdrawToDefectCartSets.remove(entry.getKey());
                }
            }
        }
    }

    private void withdrawCartSet() {
        CartSet selectedDefectCartSet = defectCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedDefectCartSet!=null) {
            Integer defectQuantity = selectedDefectCartSet.getQuantity();
            Integer withdrawQuantity = defectQuantity;
            quantityController.showAndWait(stage,withdrawQuantity,defectQuantity);
            if (quantityController.getResult()==Result.OK) {
                withdrawQuantity = quantityController.getQuantity();
                defectQuantity = defectQuantity-withdrawQuantity;
                CartSet withdrawCartSet = selectedDefectCartSet.copyCartSet();
                withdrawCartSet.setStatus(CartStatus.WITHDRAW);
                withdrawCartSet.setQuantity(withdrawQuantity);
                selectedDefectCartSet.setQuantity(defectQuantity);

                // При повторном списании той же позиции суммируем итоговую
                for (CartSet cartSet:observableWithdrawCartSets) {
                    if (cartSet.mergeCardSet(withdrawCartSet)) {
                        updateContent();
                        return;
                    }
                }


                observableWithdrawCartSets.addAll(withdrawCartSet);
                withdrawToDefectCartSets.put(withdrawCartSet, selectedDefectCartSet);

                updateContent();
            }

        }
    }

    // Обработчики для панели Списанные

    private void editWithdrawCartSet() {
        CartSet selectedWithdrawCartSet = withdrawCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedWithdrawCartSet!=null) {
            CartSet selectedDefectCartSet = withdrawToDefectCartSets.get(selectedWithdrawCartSet);
            Integer withdrawQuantity = selectedWithdrawCartSet.getQuantity();
            Integer fullQuantity = withdrawQuantity + selectedDefectCartSet.getQuantity();
            quantityController.showAndWait(stage, withdrawQuantity, fullQuantity);
            if (quantityController.getResult()==Result.OK) {
                withdrawQuantity = quantityController.getQuantity();
                selectedWithdrawCartSet.setQuantity(withdrawQuantity);
                selectedDefectCartSet.setQuantity(fullQuantity - withdrawQuantity);
                updateContent();
            }
        }
    }

    private void removeWithdrawCartSet() {
        CartSet selectedWithdrawCartSet = withdrawCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedWithdrawCartSet!=null) {
            CartSet selectedDefectCartSet = withdrawToDefectCartSets.get(selectedWithdrawCartSet);
            Integer withdrawQuantity = selectedWithdrawCartSet.getQuantity();
            Integer fullQuantity = withdrawQuantity + selectedDefectCartSet.getQuantity();
            selectedDefectCartSet.setQuantity(fullQuantity);
            withdrawToDefectCartSets.remove(selectedWithdrawCartSet);
            observableWithdrawCartSets.removeAll(selectedWithdrawCartSet);
            updateContent();
        }
    }




    // Общие обработчики


    private void saveCartSets() {

        log.info("Saving withdraw data");
        SavingTask savingTask = new SavingTask();
        BooleanBinding runningBinding = savingTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(savingTask).start();

    }


    private void clearLists() {
        observableDefectCartSets.clear();
        observableWithdrawCartSets.clear();

        withdrawToDefectCartSets.clear();
    }


    // Классы для сохранения данных в отдельном потоке

    class CheckDirectTask extends Task<Collection<Place>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
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
            addAllDefectFromDirect();

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

    class GettingDefectFromDirectTask extends Task<Collection<CartSet>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        protected Collection<CartSet> call() throws Exception {
            Collection<CartSet> cartSets = cartSetService.getCartSetsByPlace(direct);

            return cartSets;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            Collection<CartSet> cartSets = getValue();
            Iterator<CartSet> cartSetIterator = cartSets.iterator();
            while (cartSetIterator.hasNext()) {
                CartSet cartSet = cartSetIterator.next();
                if (cartSet.getStatus() != CartStatus.DEFECT) cartSetIterator.remove();
            }

            observableDefectCartSets.clear();
            observableDefectCartSets.addAll(cartSets);

        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error getting CartSets from Direct: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при чтении данных: " + exception)
                    .showException(exception);
        }

    }

    class SavingTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        protected Void call() throws Exception {

            // сохраняем поступившие наборы
            for (CartSet withdrawCartSet: observableWithdrawCartSets) {

                log.info("Adding withdraw CartSet...");
                cartSetService.addCartSet(withdrawCartSet);
                log.info("Adding withdraw CartSet complete");

                CartSet sourceCartSet = withdrawToDefectCartSets.get(withdrawCartSet);

                log.info("Updating source CartSet...");
                cartSetService.updateCartSet(sourceCartSet);
                log.info("Adding source CartSet complete");


                saveCartSetChange(sourceCartSet,withdrawCartSet, CartSetChange.Type.WITHDRAW);
            }

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Saving withdraw data complete");
            clearLists();
            Dialogs.create()
                    .owner(stage)
                    .title("Оповещение")
                    .message("Списание сохранено")
                    .showInformation();
            hideStage();

        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error saving withdraw data: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при сохранении данных: " + exception)
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


            log.info("Saving change... ");
            cartSetChangeService.addChange(cartSetChange);
            log.info("Saving change complete");

        }

    }


}
