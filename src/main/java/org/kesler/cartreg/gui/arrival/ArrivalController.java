package org.kesler.cartreg.gui.arrival;

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
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.*;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.cartset.CartSetController;
import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.util.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * Контроллер для отслеживания поступлений картриджей
 */
@Component
public class ArrivalController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @FXML protected Label placeLabel;
    @FXML protected TableView<CartSet> cartSetTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;

    @Autowired
    protected PlaceListController placeListController;

    @Autowired
    protected CartSetController cartSetController;

    @Autowired
    protected CartSetService cartSetService;

    @Autowired
    protected CartSetChangeService cartSetChangeService;

    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();
    private Place place;

    @FXML
    protected void initialize() {
        cartSetTableView.setItems(observableCartSets);
    }

    @Override
    public void show(Window owner) {
        observableCartSets.clear();
        super.show(owner, "Поступление картриджей");
    }

    @FXML
    protected void handleSelectPlaceButtonAction(ActionEvent ev) {
        selectPlace();
    }

    @FXML
    protected void handleAddButtonAction(ActionEvent ev) {
        addCartSet();
    }

    @FXML
    protected void handleEditButtonAction(ActionEvent ev) {
        editCartSet();
    }

    @FXML
    protected void handleCartSetTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            editCartSet();
        }
    }

    @FXML
    protected void handleRemoveButtonAction(ActionEvent ev) {
        removeCartSet();
    }

    @Override
    protected void updateContent() {
        placeLabel.setText(place==null?"Не определено":place.getCommonName());
        FXUtils.updateObservableList(observableCartSets);
    }

    @Override
    protected void handleOk() {
        log.info("Handle OK action");
        saveCartSets();
    }

    private void selectPlace() {
        log.info("Handle select place");
        Place.Type[] placeTypes = {Place.Type.DIRECT,Place.Type.STORAGE};
        placeListController.showAndWaitSelect(stage,placeTypes);
        if (placeListController.getResult()==Result.OK) {
            place = placeListController.getSelectedItem();
            updateContent();
        }
    }

    private void addCartSet() {
        log.info("Handle add CartSet");
        CartSet cartSet = new CartSet();
        cartSet.setStatus(CartStatus.NEW);
        cartSet.setQuantity(50);
        cartSet.setPlace(place);
        cartSetController.showAndWait(stage, cartSet);
        if (cartSetController.getResult()==Result.OK) {
            observableCartSets.add(cartSet);
            cartSetTableView.getSelectionModel().select(cartSet);
        }
    }

    private void editCartSet() {
        log.info("Handle edit CartSet");
        CartSet selectedCartSet = cartSetTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            cartSetController.showAndWait(stage, selectedCartSet);
            if (cartSetController.getResult()==Result.OK) {
                FXUtils.updateObservableList(observableCartSets);
            }
            cartSetTableView.getSelectionModel().select(selectedCartSet);
        }
    }

    private void removeCartSet() {
        log.info("Handle remove CartSet");
        CartSet selectedCartSet = cartSetTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            observableCartSets.remove(selectedCartSet);
        }
    }


    private void saveCartSets() {
        log.info("Saving arrival data ");
        SavingTask savingTask = new SavingTask();

        BooleanBinding runningBinding = savingTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(savingTask).start();

    }

    private void clearLists() {
        observableCartSets.clear();
    }


    // Классы для сохранения данных в отдельном потоке

    class SavingTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Void call() throws Exception {


            for(CartSet cartSet:observableCartSets) {

                log.info("Adding CartSet: "
                        + cartSet.getModel()
                        + " (" + cartSet.getStatusDesc() + ") - "
                        + cartSet.getQuantity());
                cartSetService.addCartSet(cartSet);
                log.info("Adding CartSet complete");

                // Сохраняем перемещение
                CartSetChange cartSetChange = new CartSetChange();

                cartSetChange.setType(CartSetChange.Type.ARRIVAL);
                cartSetChange.setCartType(cartSet.getType());
                cartSetChange.setFromPlace(null);
                cartSetChange.setToPlace(cartSet.getPlace());
                cartSetChange.setFromStatus(CartStatus.NONE);
                cartSetChange.setToStatus(cartSet.getStatus());
                cartSetChange.setQuantity(cartSet.getQuantity());
                cartSetChange.setChangeDate(new Date());


                log.info("Saving change... ");
                cartSetChangeService.addChange(cartSetChange);
                log.info("Saving change complete");

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
            log.error("Error saving arrival data: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при сохранении данных: " + exception)
                    .showException(exception);
        }

    }


}
