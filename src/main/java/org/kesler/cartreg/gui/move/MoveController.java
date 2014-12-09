package org.kesler.cartreg.gui.move;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.gui.util.QuantityController;
import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.util.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер окна перемещения
 */
@Component
public class MoveController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected Label sourcePlaceLabel;
    @FXML protected Label destinationPlaceLabel;
    @FXML protected ProgressIndicator updateProgressIndicator;

    @FXML protected TableView<CartSet> cartSetsTableView;

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

    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();

    private Place sourcePlace;
    private Place destinationPlace;

    private Map<CartSet, CartSet> toFromCartSets = new HashMap<CartSet, CartSet>();

    @FXML
    protected void initialize() {
        cartSetsTableView.setItems(observableCartSets);
    }

    @Override
    public void show(Window owner) {
        clearLists();
        Image icon = new Image(this.getClass().getResourceAsStream("/images/transform.png"));

        super.show(owner, "Перемещение", icon);
    }

    @FXML
    protected void handleSelectSourcePlaceButtonAction(ActionEvent ev) {
        selectSourcePlace();
    }

    @FXML
    protected void handleSelectDestinationPlaceButtonAction(ActionEvent ev) {
        selectDestinationPlace();
    }

    @FXML
    protected void handleAddCartSetButtonAction(ActionEvent ev) {
        addCartSet();
    }

    @FXML
    protected void handleEditCartSetButtonAction(ActionEvent ev) {
        editCartSet();
    }

    @FXML
    protected void handleRemoveCartSetButtonAction(ActionEvent ev) {
        removeCartSet();
    }

    @Override
    protected void handleOk() {
        if (observableCartSets.size()==0) {
            Dialogs.create()
                    .owner(stage)
                    .title("Внимание")
                    .message("Ничего не выбрано")
                    .showWarning();
            return;
        }

        if (!checkDestination()) return;

        for (CartSet cartSet: observableCartSets) {
            cartSet.setPlace(destinationPlace);
        }

        String message = "Переместить картриджи?";
        for (CartSet cartSet: observableCartSets) {
            message += "\n" + cartSet.getModel() +" (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity()
                    + " шт " + sourcePlace.getName() + " - > " + destinationPlace.getName();
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
    protected void updateContent() {
        sourcePlaceLabel.setText(sourcePlace==null?"Не определено":sourcePlace.getCommonName());
        destinationPlaceLabel.setText(destinationPlace==null?"Не определено":destinationPlace.getCommonName());
    }


    private void selectSourcePlace() {
        log.info("Handle select source place");
        placeListController.showAndWaitSelect(stage);
        if (placeListController.getResult()==Result.OK) {
            sourcePlace = placeListController.getSelectedItem();
            log.info("Source place set:" + sourcePlace.getCommonName());
            updateContent();
        }
    }

    private void selectDestinationPlace() {
        log.info("Handle select destination place");
        placeListController.showAndWaitSelect(stage);
        if (placeListController.getResult()==Result.OK) {
            destinationPlace = placeListController.getSelectedItem();
            log.info("Destination place set:" + sourcePlace.getCommonName());
            updateContent();
        }
    }

    private void addCartSet() {
        log.info("Handle add CartSet");
        if (!checkSource()) return;
        placeCartSetsController.showAndWaitSelect(stage, sourcePlace);
        if (placeCartSetsController.getResult()==Result.OK) {
            CartSet sourceCartSet = placeCartSetsController.getSelectedItem();
            // если пытаемся добавить существующий - то редактируем
            for (Map.Entry<CartSet,CartSet> toFromEntry:toFromCartSets.entrySet()) {
                if (toFromEntry.getValue().equals(sourceCartSet)) {
                    cartSetsTableView.getSelectionModel().select(observableCartSets.indexOf(toFromEntry.getKey()));
                    editCartSet();
                    return;
                }
            }

            CartSet moveCartSet = sourceCartSet.copyCartSet();

            quantityController.showAndWait(stage,sourceCartSet.getQuantity(),sourceCartSet.getQuantity());
            if (quantityController.getResult()==Result.OK) {
                Integer sourceQuantity = sourceCartSet.getQuantity();
                Integer moveQuantity = quantityController.getQuantity();
                moveCartSet.setQuantity(moveQuantity);
                sourceCartSet.setQuantity(sourceQuantity-moveQuantity);

                observableCartSets.addAll(moveCartSet);

                toFromCartSets.put(moveCartSet,sourceCartSet);
            }
        }
    }

    private void editCartSet() {
        log.info("Handle edit CartSet");
        CartSet selectedMoveCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet sourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            Integer sourceQuantity = sourceCartSet.getQuantity() + selectedMoveCartSet.getQuantity();
            Integer moveQuantity = selectedMoveCartSet.getQuantity();
            quantityController.showAndWait(stage, moveQuantity,sourceQuantity);
            if (quantityController.getResult()==Result.OK) {
                moveQuantity = quantityController.getQuantity();
                selectedMoveCartSet.setQuantity(moveQuantity);
                sourceCartSet.setQuantity(sourceQuantity-moveQuantity);

                FXUtils.updateObservableList(observableCartSets);
            }
        }
    }

    private void removeCartSet() {
        log.info("Handle remove CartSet");
        CartSet selectedMoveCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedMoveCartSet!=null) {
            CartSet sourceCartSet = toFromCartSets.get(selectedMoveCartSet);
            Integer sourceQuantity = sourceCartSet.getQuantity() + selectedMoveCartSet.getQuantity();
            sourceCartSet.setQuantity(sourceQuantity);

            toFromCartSets.remove(selectedMoveCartSet);
            observableCartSets.removeAll(selectedMoveCartSet);
        }
    }

    private void saveCartSets() {

        log.info("Saving move data...");
        SavingTask savingTask = new SavingTask();
        BooleanBinding runningBinding = savingTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(savingTask).start();


    }


    private void clearLists() {
        observableCartSets.clear();
        toFromCartSets.clear();
        sourcePlace = null;
        destinationPlace = null;
    }

    private boolean checkSource() {
        if (sourcePlace==null) {
            log.info("Source not set - show select dialog");
            Dialogs.create()
                    .owner(stage)
                    .title("Внимание")
                    .message("Непонятно откуда перемещаем, выберите источник")
                    .showWarning();
            placeListController.showAndWaitSelect(stage);
            if (placeListController.getResult()==Result.OK) {
                sourcePlace = placeListController.getSelectedItem();
                updateContent();
            } else {
                return false;
            }
        }

        log.info("Source is ok");
        return true;
    }


    private boolean checkDestination() {
        if (destinationPlace==null) {
            log.info("Destination not set - show select dialog");
            Dialogs.create()
                    .owner(stage)
                    .title("Внимание")
                    .message("Непонятно куда перемещаем, выберите назначение")
                    .showWarning();
            placeListController.showAndWaitSelect(stage);
            if (placeListController.getResult()==Result.OK) {
                destinationPlace = placeListController.getSelectedItem();
                updateContent();
            } else {
                return false;
            }
        }
        log.info("Destination is ok");
        return true;
    }



    // Классы для сохранения данных в отдельном потоке

    class SavingTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        protected Void call() throws Exception {

            // сохраняем поступившие наборы
            for (CartSet moveCartSet:observableCartSets) {

                log.info("Adding move CartSet...");
                cartSetService.addCartSet(moveCartSet);
                log.info("Adding move CartSet complete");

                CartSet sourceCartSet = toFromCartSets.get(moveCartSet);

                log.info("Updating source CartSet...");
                cartSetService.updateCartSet(sourceCartSet);
                log.info("Updating source CartSet complete");

                saveCartSetChange(sourceCartSet, moveCartSet);
            }


            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Saving move data complete ");
            clearLists();
            Dialogs.create()
                    .owner(stage)
                    .title("Информация")
                    .message("Перемещение сохранено")
                    .showInformation();
            hideStage();

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


        private void saveCartSetChange(CartSet fromCartSet, CartSet toCartSet) {
            CartSetChange cartSetChange = new CartSetChange();
            cartSetChange.setType(CartSetChange.Type.MOVE);
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
