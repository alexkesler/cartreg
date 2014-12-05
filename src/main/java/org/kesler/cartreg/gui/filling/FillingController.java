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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Контроллер для окна заправки
 */
@Component
public class FillingController extends AbstractController {

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
            Place.Type[] placeTypes = {Place.Type.DIRECT};
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
            Place.Type[] placeTypes = {Place.Type.DIRECT};
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

    @Override
    protected void updateContent() {
        FXUtils.updateObservableList(observableEmptyCartSets);
        FXUtils.updateObservableList(observableFilledCartSets);
        FXUtils.updateObservableList(observableDefectCartSet);
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
            clearLists();
            Dialogs.create()
                    .owner(stage)
                    .title("Оповещение")
                    .message("Заправлено")
                    .showInformation();
            stage.hide();
        }

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

                observableDefectCartSet.addAll(defectCartSet);
                defectToEmptyCartSet.put(defectCartSet, selectedEmptyCartSet);

                updateContent();
            }

        }
    }

    // Обработчики для панели Заправленые




    // Обработчики для панели Неисправные




    // Общие обработчики


    private void saveCartSets() {

        // сохраняем поступившие наборы
        for (CartSet filledCartSet:observableFilledCartSets) {

            log.info("Adding filled CartSet...");
            AddCartSetTask addCartSetTask = new AddCartSetTask(filledCartSet);
            BooleanBinding runningBinding = addCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addCartSetTask).start();

            CartSet sourceCartSet = filledToEmptyCartSets.get(filledCartSet);

            log.info("Updating source CartSet...");
            UpdateCartSetTask updateCartSetTask = new UpdateCartSetTask(sourceCartSet);
            runningBinding = updateCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(updateCartSetTask).start();


            saveCartSetChange(sourceCartSet,filledCartSet, CartSetChange.Type.FILL);
        }
        // сохраняем отправленные наборы
        for (CartSet defectCartSet:observableDefectCartSet) {
            log.info("Adding defect CartSet...");
            AddCartSetTask addCartSetTask = new AddCartSetTask(defectCartSet);
            BooleanBinding runningBinding = addCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addCartSetTask).start();

            CartSet sourceCartSet = defectToEmptyCartSet.get(defectCartSet);

            log.info("Updating source CartSet...");
            UpdateCartSetTask updateCartSetTask = new UpdateCartSetTask(sourceCartSet);
            runningBinding = updateCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(updateCartSetTask).start();

            saveCartSetChange(sourceCartSet, defectCartSet, CartSetChange.Type.DEFECT);
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
        observableEmptyCartSets.clear();
        observableFilledCartSets.clear();
        observableDefectCartSet.clear();

        filledToEmptyCartSets.clear();
        defectToEmptyCartSet.clear();
    }


    // Классы для обновления данных в отдельном потоке

    class AddCartSetTask extends Task<Void> {
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


    class SaveChangeTask extends Task<Void> {
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
