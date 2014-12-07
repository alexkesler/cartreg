package org.kesler.cartreg.gui.filling;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
 * Контроллер для окна заправки
 */
@Component
public class FillingController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected TableView<CartSet> emptyCartSetsTableView;
    @FXML protected TableView<CartSet> filledCartSetsTableView;
    @FXML protected TableView<CartSet>  defectCartSetsTableView;
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

    private final ObservableList<CartSet> observableEmptyCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableFilledCartSets = FXCollections.observableArrayList();
    private final ObservableList<CartSet> observableDefectCartSet = FXCollections.observableArrayList();

    private Place direct;

    private final Map<CartSet,CartSet> filledToEmptyCartSets = new HashMap<CartSet, CartSet>();
    private final Map<CartSet, CartSet> defectToEmptyCartSet = new HashMap<CartSet, CartSet>();

    @FXML
    protected void initialize() {
        emptyCartSetsTableView.setItems(observableEmptyCartSets);
        filledCartSetsTableView.setItems(observableFilledCartSets);
        defectCartSetsTableView.setItems(observableDefectCartSet);
    }

    @Override
    public void show(Window owner) {

        clearLists();

        super.show(owner,"Заправка картриджей");
        checkDirect();
    }


    // Управление Панелью "На заправку"

    @FXML
    protected void handleAddEmptyCartSetButtonAction(ActionEvent ev) {
        addEmptyCartSet();
    }

    @FXML
    protected void handleRemoveEmptyCartSetButtonAction(ActionEvent ev) {
        removeEmptyCartSet();
    }

    @FXML
    protected void handleFillMenuItemAction(ActionEvent ev) {
        fillCartSet();
    }

    @FXML
    protected void handleDefectMenuItemAction(ActionEvent ev) {
        defectCartSet();
    }

    @FXML
    protected void handleFillButtonAction(ActionEvent ev) {
        fillCartSet();
    }

    @FXML
    protected void handleDefectButtonAction(ActionEvent ev) {
        defectCartSet();
    }


    // Управление панелью "Заправленные"

    @FXML
    protected void handleEditFiledCartSetButtonAction(ActionEvent ev) {
        editFilledCartSet();
    }

    @FXML
    protected void handleFiledCartSetsTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editFilledCartSet();
        }
    }

    @FXML
    protected void handleRemoveFiledCartSetButtonAction(ActionEvent ev) {
        removeFilledCartSet();
    }


    // Управление панелью "Неисправные"

    @FXML
    protected void handleEditDefectCartSetButtonAction(ActionEvent ev) {
        editDefectCartSet();
    }

    @FXML
    protected void handleDefectCartSetsTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editDefectCartSet();
        }
    }

    @FXML
    protected void handleRemoveDefectCartSetButtonAction(ActionEvent ev) {
        removeDefectCartSet();
    }

    @Override
    protected void updateContent() {

        FXUtils.triggerUpdateTableView(emptyCartSetsTableView);
        FXUtils.triggerUpdateTableView(filledCartSetsTableView);
        FXUtils.triggerUpdateTableView(defectCartSetsTableView);

    }


    @Override
    protected void handleOk() {
        String message = "Заправить картриджи?";
        message += "\n Заправлено:";
        for (CartSet cartSet: observableFilledCartSets) {
            message += "\n" + cartSet.getModel() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity() + " шт";
        }
        message += "\n Неисправно:";

        for (CartSet cartSet: observableDefectCartSet) {
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

    //// Приватные методы для отработки логики

    private void checkDirect() {
        log.info("Checking direct...");
        /// Проверяем дирекцию в отдельном потоке
        CheckDirectTask checkDirectTask = new CheckDirectTask();
        BooleanBinding runningBinding = checkDirectTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(checkDirectTask).start();
    }


    private void addAllEmptyFromDirect() {
        log.info("Getting empty CartSets from direct");
        GettingEmptyFromDirectTask gettingEmptyFromDirectTask = new GettingEmptyFromDirectTask();
        BooleanBinding runningBinding = gettingEmptyFromDirectTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(gettingEmptyFromDirectTask).start();
    }


    /// Обработчики для панели Пустые картриджи

    private void addEmptyCartSet() {
        CartStatus[] statuses = {CartStatus.EMPTY};
        placeCartSetsController.showAndWaitSelect(stage,direct,statuses);
        if (placeCartSetsController.getResult()==Result.OK) {
            CartSet cartSet = placeCartSetsController.getSelectedItem();
            if (!observableEmptyCartSets.contains(cartSet))
                observableEmptyCartSets.addAll(cartSet);
        }
    }

    private void removeEmptyCartSet() {
        CartSet selectedCartSet = emptyCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            observableEmptyCartSets.removeAll(selectedCartSet);
            for (Map.Entry<CartSet,CartSet> entry:filledToEmptyCartSets.entrySet()) {
                if (entry.getValue().equals(selectedCartSet)) {
                    observableFilledCartSets.removeAll(entry.getKey());
                    filledToEmptyCartSets.remove(entry.getKey());
                }
            }
        }
    }

    private void fillCartSet() {
        CartSet selectedEmptyCartSet = emptyCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedEmptyCartSet!=null) {
            Integer emptyQuantity = selectedEmptyCartSet.getQuantity();
            Integer filledQuantity = emptyQuantity;
            quantityController.showAndWait(stage,filledQuantity,emptyQuantity);
            if (quantityController.getResult()==Result.OK) {
                filledQuantity = quantityController.getQuantity();
                emptyQuantity = emptyQuantity-filledQuantity;
                CartSet filledCartSet = selectedEmptyCartSet.copyCartSet();
                filledCartSet.setStatus(CartStatus.FILLED);
                filledCartSet.setQuantity(filledQuantity);
                selectedEmptyCartSet.setQuantity(emptyQuantity);

                for (CartSet cartSet:observableFilledCartSets) {
                    if (cartSet.mergeCardSet(filledCartSet)) {
                        updateContent();
                        return;
                    }
                }

                observableFilledCartSets.addAll(filledCartSet);
                filledToEmptyCartSets.put(filledCartSet, selectedEmptyCartSet);

                updateContent();
            }

        }
    }

    private void defectCartSet() {

        CartSet selectedEmptyCartSet = emptyCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedEmptyCartSet!=null) {
            Integer emptyQuantity = selectedEmptyCartSet.getQuantity();
            Integer defectQuantity = emptyQuantity;
            quantityController.showAndWait(stage,defectQuantity,emptyQuantity);
            if (quantityController.getResult()==Result.OK) {
                defectQuantity = quantityController.getQuantity();
                emptyQuantity = emptyQuantity-defectQuantity;
                CartSet defectCartSet = selectedEmptyCartSet.copyCartSet();
                defectCartSet.setStatus(CartStatus.DEFECT);
                defectCartSet.setQuantity(defectQuantity);
                selectedEmptyCartSet.setQuantity(emptyQuantity);

                for (CartSet cartSet:observableDefectCartSet) {
                    if (cartSet.mergeCardSet(defectCartSet)) {
                        updateContent();
                        return;
                    }
                }

                observableDefectCartSet.addAll(defectCartSet);
                defectToEmptyCartSet.put(defectCartSet, selectedEmptyCartSet);

                updateContent();
            }

        }
    }

    // Обработчики для панели Заправленые

    private void editFilledCartSet() {
        CartSet selectedFilledCartSet = filledCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedFilledCartSet!=null) {
            CartSet selectedEmptyCartSet = filledToEmptyCartSets.get(selectedFilledCartSet);
            Integer filledQuantity = selectedFilledCartSet.getQuantity();
            Integer fullquantity = filledQuantity + selectedEmptyCartSet.getQuantity();
            quantityController.showAndWait(stage, filledQuantity, fullquantity);
            if (quantityController.getResult()==Result.OK) {
                filledQuantity = quantityController.getQuantity();
                selectedFilledCartSet.setQuantity(filledQuantity);
                selectedEmptyCartSet.setQuantity(fullquantity-filledQuantity);
                updateContent();
            }
        }
    }

    private void removeFilledCartSet() {
        CartSet selectedFilledCartSet = filledCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedFilledCartSet!=null) {
            CartSet selectedEmptyCartSet = filledToEmptyCartSets.get(selectedFilledCartSet);
            Integer filledQuantity = selectedFilledCartSet.getQuantity();
            Integer fullquantity = filledQuantity + selectedEmptyCartSet.getQuantity();
            selectedEmptyCartSet.setQuantity(fullquantity);
            filledToEmptyCartSets.remove(selectedFilledCartSet);
            observableFilledCartSets.removeAll(selectedFilledCartSet);
            updateContent();
        }
    }


    // Обработчики для панели Неисправные

    private void editDefectCartSet() {
        CartSet selectedDefectCartSet = defectCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedDefectCartSet!=null) {
            CartSet selectedEmptyCartSet = defectToEmptyCartSet.get(selectedDefectCartSet);
            Integer defectQuantity = selectedDefectCartSet.getQuantity();
            Integer fullquantity = defectQuantity + selectedEmptyCartSet.getQuantity();
            quantityController.showAndWait(stage, defectQuantity, fullquantity);
            if (quantityController.getResult()==Result.OK) {
                defectQuantity = quantityController.getQuantity();
                selectedDefectCartSet.setQuantity(defectQuantity);
                selectedEmptyCartSet.setQuantity(fullquantity-defectQuantity);
                updateContent();
            }
        }

    }

    private void removeDefectCartSet() {
        CartSet selectedDefectCartSet = defectCartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedDefectCartSet!=null) {
            CartSet selectedEmptyCartSet = defectToEmptyCartSet.get(selectedDefectCartSet);
            Integer defectQuantity = selectedDefectCartSet.getQuantity();
            Integer fullquantity = defectQuantity + selectedEmptyCartSet.getQuantity();
            selectedEmptyCartSet.setQuantity(fullquantity);
            defectToEmptyCartSet.remove(selectedDefectCartSet);
            observableDefectCartSet.removeAll(selectedDefectCartSet);
            updateContent();
        }
    }



    // Общие обработчики


    private void saveCartSets() {


        log.info("Saving filling data.. ");
        SavingTask savingTask = new SavingTask();
        BooleanBinding runningBinding = savingTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(savingTask).start();



    }


    private void clearLists() {
        observableEmptyCartSets.clear();
        observableFilledCartSets.clear();
        observableDefectCartSet.clear();

        filledToEmptyCartSets.clear();
        defectToEmptyCartSet.clear();
    }


    // Классы для обновления данных в отдельном потоке

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
            addAllEmptyFromDirect();

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

    class GettingEmptyFromDirectTask extends Task<Collection<CartSet>> {
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
                if (cartSet.getStatus() != CartStatus.EMPTY) cartSetIterator.remove();
            }

            observableEmptyCartSets.clear();
            observableEmptyCartSets.addAll(cartSets);

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
            for (CartSet filledCartSet:observableFilledCartSets) {

                log.info("Adding filled CartSet: "
                        + filledCartSet.getModel()
                        + " (" + filledCartSet.getStatusDesc() + ") - "
                        + filledCartSet.getQuantity());
                cartSetService.addCartSet(filledCartSet);
                log.info("Adding filled CartSet complete");

                CartSet sourceCartSet = filledToEmptyCartSets.get(filledCartSet);
                saveCartSetChange(sourceCartSet, filledCartSet, CartSetChange.Type.FILL);
            }
            // сохраняем отправленные наборы
            for (CartSet defectCartSet:observableDefectCartSet) {
                log.info("Adding defect CartSet: "
                        + defectCartSet.getModel()
                        + " (" + defectCartSet.getStatusDesc() + ") - "
                        + defectCartSet.getQuantity());
                cartSetService.addCartSet(defectCartSet);
                log.info("Adding defect CartSet complete");

                CartSet sourceCartSet = defectToEmptyCartSet.get(defectCartSet);
                saveCartSetChange(sourceCartSet, defectCartSet, CartSetChange.Type.DEFECT);
            }


            // Сохраняем исходные наборы

            for (CartSet sourceCartSet: observableEmptyCartSets) {
                log.info("Updating source CartSet: "
                        + sourceCartSet.getModel()
                        + " (" + sourceCartSet.getStatusDesc() + ") - "
                        + sourceCartSet.getQuantity());
                cartSetService.updateCartSet(sourceCartSet);
                log.info("Updating source CartSet complete");

            }



            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Saving filling data complete");
            clearLists();
            Dialogs.create()
                    .owner(stage)
                    .title("Оповещение")
                    .message("Заправка/дефектовка сохранена")
                    .showInformation();
            hideStage();

        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error saving filling data: " + exception, exception);
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
