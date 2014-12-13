package org.kesler.cartreg.gui.placecartsetsselect;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.gui.util.QuantityController;
import org.kesler.cartreg.service.CartSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Контроллер для выбора картриджей в определенном размещении
 */
@Component
public class PlaceCartSetsSelectController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Place place;
    private CartStatus[] statuses;

    @FXML protected Label placeLabel;
    @FXML protected TableView<CheckableCartSet> cartSetsTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;

    private final ObservableList<CheckableCartSet> observableCheckableCartSets = FXCollections.observableArrayList();
    private final Map<CartSet,CartSet> selectedToSourceCartSets = new HashMap<>();

    @Autowired
    private CartSetService cartSetService;

    @Autowired
    private QuantityController quantityController;

    @Autowired
    private PlaceCartSetsController placeCartSetsController;


    @FXML
    protected void initialize() {
        SortedList<CheckableCartSet> sortedCheckableCartSets = new SortedList<CheckableCartSet>(observableCheckableCartSets, new CheckableCartSetComparator());
        cartSetsTableView.setItems(sortedCheckableCartSets);
    }

    public void showAndWait(Window owner, Place place) {
        log.info("ShowAndWait for place " + place.getCommonName());
        this.place = place;
        selectedToSourceCartSets.clear();
        statuses = null;
        super.showAndWait(owner, "Выберите картриджи");
    }

    public void showAndWait(Window owner, Place place, CartStatus[] statuses) {
        log.info("ShowAndWait for place " + place.getCommonName());
        this.place = place;
        selectedToSourceCartSets.clear();
        this.statuses = statuses;
        super.showAndWait(owner, "Выберите картриджи");
    }

    public void showAndWait(Window owner, Place place, Map<CartSet,CartSet> selectedToSourceCartSets) {
        log.info("ShowAndWait for place " + place.getCommonName() + " and selectedCartSets");
        this.place = place;
        statuses = null;
        this.selectedToSourceCartSets.clear();
        this.selectedToSourceCartSets.putAll(selectedToSourceCartSets);
        super.showAndWait(owner, "Выберите картриджи");
    }

    public void showAndWait(Window owner, Place place, CartStatus[] statuses, Map<CartSet,CartSet> selectedToSourceCartSets) {
        log.info("ShowAndWait for place " + place.getCommonName() + " and selectedCartSets");
        this.place = place;
        this.statuses = statuses;
        this.selectedToSourceCartSets.clear();
        this.selectedToSourceCartSets.putAll(selectedToSourceCartSets);
        super.showAndWait(owner, "Выберите картриджи");
    }


    @Override
    protected void updateContent() {

        placeLabel.setText(place.getCommonName());

        observableCheckableCartSets.clear();
        UpdateTask updateTask = new UpdateTask();

        BooleanBinding runningBinding = updateTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(updateTask).start();

    }


    public Map<CartSet,CartSet> getSelectedToSourceCartSetsMap() {

        selectedToSourceCartSets.clear();

        for(CheckableCartSet checkableCartSet:observableCheckableCartSets) {
            if (checkableCartSet.getChecked()) {
                CartSet sourceCartSet = checkableCartSet.getSourceCartSet();
                CartSet selectedCartSet = sourceCartSet.copyCartSet();
                selectedCartSet.setQuantity(checkableCartSet.getCheckedQuantity());
                selectedToSourceCartSets.put(selectedCartSet,sourceCartSet);
            }
        }

        return selectedToSourceCartSets;
    }


    @FXML
    protected void handlePlaceCartSetsButtonAction(ActionEvent ev) {
        showPlaceCartSetsDialog();
    }

    @FXML
    protected void handleCartSetsTableViewMouseClick(MouseEvent ev) {

        if (ev.getClickCount() == 2) {
            CheckableCartSet checkableCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
            if (checkableCartSet != null) {
                if (checkableCartSet.getChecked()) editCheckableCartSet(checkableCartSet);
                else checkableCartSet.setChecked(true);
            }
        }
    }


    private void showPlaceCartSetsDialog() {
        placeCartSetsController.showAndWait(stage, place);
        updateContent();
    }


    private void editCheckableCartSet(CheckableCartSet checkableCartSet) {
        quantityController.showAndWait(stage,checkableCartSet.getCheckedQuantity(),checkableCartSet.getSourceQuantity());
        if (quantityController.getResult()==Result.OK) {
            checkableCartSet.setCheckedQuantity(quantityController.getQuantity());
        }
    }



    class UpdateTask extends Task<Collection<CartSet>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        protected Collection<CartSet> call() throws Exception {
            log.debug("Reading CartSets for place: " + place.getCommonName());
            Collection<CartSet> cartSets = cartSetService.getCartSetsByPlace(place);
            log.debug("Server returned " + cartSets.size() + " CartSets");
            return cartSets;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            Collection<CartSet> sourceCartSets = getValue();

            log.debug("Update list...");

            for (CartSet sourceCartSet:sourceCartSets) {
                CheckableCartSet checkableCartSet = null;
                // проверяем есть ли в списке выбранных наборов набор с нашим источником
                for (Map.Entry<CartSet,CartSet> selectedToSourceEntry:selectedToSourceCartSets.entrySet()) {
                    if(selectedToSourceEntry.getValue().equals(sourceCartSet)) {
                        CartSet selectedCartSet = selectedToSourceEntry.getKey();
                        // назначаем ему нужное количество и выбираем
                        checkableCartSet = new CheckableCartSet(sourceCartSet,selectedCartSet.getQuantity(), true);
                        break;
                    }
                }
                if (checkableCartSet==null) checkableCartSet = new CheckableCartSet(sourceCartSet);

                final CheckableCartSet finalCheckableCartSet = checkableCartSet;
                // добавляем в список
                observableCheckableCartSets.add(finalCheckableCartSet);
                // назначаем слушателей - для вызова окошка с количеством
                finalCheckableCartSet.checkedProperty().addListener(new WeakChangeListener<>((observable, oldValue, newValue) -> {
                    if (newValue) {
                        editCheckableCartSet(finalCheckableCartSet);
                    } else {
                        finalCheckableCartSet.setCheckedQuantity(0);
                    }
                }));

            }
            log.info("Update CartSets complete");

        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error updating CartSets: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при чтении данных: " + exception)
                    .showException(exception);

        }
    }

}
