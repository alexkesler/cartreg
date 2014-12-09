package org.kesler.cartreg.gui.cartsetchanges;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.place.PlaceComparator;
import org.kesler.cartreg.service.CartSetChangeService;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.function.Predicate;

/**
 * Контроллер для списка изменений
 */
@Component
public class CartSetChangesController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected DatePicker fromDatePicker;
    @FXML protected DatePicker toDatePicker;
    @FXML protected ComboBox<CartSetChange.Type> actionComboBox;
    @FXML protected ComboBox<Place> fromPlaceComboBox;
    @FXML protected ComboBox<Place> toPlaceComboBox;


    @FXML protected TableView<CartSetChange> cartSetChangesTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;


    @Autowired
    protected CartSetChangeService cartSetChangeService;

    @Autowired
    protected PlaceService placeService;

    private final ObservableList<CartSetChange> observableChanges = FXCollections.observableArrayList();
    private final FilteredList<CartSetChange> filteredChanges = new FilteredList<CartSetChange>(observableChanges);
    private final SortedList<CartSetChange> sortedChanges = new SortedList<CartSetChange>(filteredChanges,new CartSetChangeComparator());

    @FXML
    protected void initialize() {

        cartSetChangesTableView.setItems(sortedChanges);
        actionComboBox.getItems().add(null);
        actionComboBox.getItems().addAll(CartSetChange.Type.values());
        actionComboBox.setCellFactory(new ActionTypeCellFactory());
        actionComboBox.setButtonCell(new ActionTypeListCell());
        fromPlaceComboBox.setCellFactory(new PlaceCellFactory());
        fromPlaceComboBox.setButtonCell(new PlaceListCell());
        toPlaceComboBox.setCellFactory(new PlaceCellFactory());
        toPlaceComboBox.setButtonCell(new PlaceListCell());

    }

    @Override
    public void show(Window owner) {
        clearFilters();
        super.showFullScreen(owner, "Перемещения");
        updatePlaceComboBoxes();
    }


    @Override
    protected void updateContent() {

        log.info("Updating List");
        UpdateTask updateListTask = new UpdateTask();

        BooleanBinding runningBinding = updateListTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        observableChanges.clear();
        new Thread(updateListTask).start();

    }


    @FXML
    protected void handleChangeFilterAction(ActionEvent ev) {
        log.info("Handle update filters action");
        updateFilters();
    }

    @FXML
    protected void handleRemoveCartSetChangeMenuItemAction(ActionEvent ev) {
        CartSetChange cartSetChange = cartSetChangesTableView.getSelectionModel().getSelectedItem();
        if (cartSetChange!=null) {
            removeCartSetChange(cartSetChange);
        }
    }

    private void updateFilters() {
        Predicate<CartSetChange> totalPredicate = new EmptyPredicate();

        LocalDate fromLocalDate = fromDatePicker.getValue();
        if (fromLocalDate!=null) {
            Date fromDate = DateUtil.localDateToDateBegDay(fromLocalDate);
            log.debug("FromDate is set: " + fromDate);
            totalPredicate = totalPredicate.and(new FromDatePredicate(fromDate));
        }

        LocalDate toLocalDate = toDatePicker.getValue();
        if (toLocalDate!=null) {
            Date toDate = DateUtil.localDateToDateEndDay(toLocalDate);
            log.debug("ToDate is set: " + toDate);
            totalPredicate = totalPredicate.and(new ToDatePredicate(toDate));
        }

        CartSetChange.Type actionType = actionComboBox.getValue();
        if (actionType!=null) {
            log.debug("ActionType is set: " + actionType.getDesc());
            totalPredicate = totalPredicate.and(new ActionTypePredicate(actionType));
        }

        Place fromPlace = fromPlaceComboBox.getValue();
        if (fromPlace!=null) {
            log.debug("FromPlace is set: " + fromPlace.getCommonName());
            totalPredicate = totalPredicate.and(new FromPlacePredicate(fromPlace));
        }

        Place toPlace = toPlaceComboBox.getValue();
        if (toPlace!=null) {
            log.debug("ToPlace is set: " + toPlace.getCommonName());
            totalPredicate = totalPredicate.and(new ToPlacePredicate(toPlace));
        }


        filteredChanges.setPredicate(totalPredicate);
        log.info("In filtered list " + filteredChanges.size() + " items");
    }

    private void clearFilters() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        actionComboBox.setValue(null);
        fromPlaceComboBox.setValue(null);
        toPlaceComboBox.setValue(null);
    }

    private void updatePlaceComboBoxes() {
        UpdatePlacesTask updatePlacesTask = new UpdatePlacesTask();
        BooleanBinding runningBinding = updatePlacesTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(updatePlacesTask).start();

    }



    private void removeCartSetChange(CartSetChange cartSetChange) {
        RemoveTask removeTask = new RemoveTask(cartSetChange);
        BooleanBinding runningBinding = removeTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(removeTask).start();

    }


    // Классы для обновления данных в отдельном потоке

    class UpdateTask extends Task<Collection<CartSetChange>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Collection<CartSetChange> call() throws Exception {
            log.debug("Updating list...");

            Collection<CartSetChange> changes = cartSetChangeService.getAllChanges();
            log.debug("Server return " + changes.size() + " changes");
            return changes;
        }

        @Override
        protected void succeeded() {
            Collection<CartSetChange> places = getValue();

            log.debug("Update observableList ...");
            observableChanges.addAll(places);

            log.info("List update complete.");
        }

        @Override
        protected void failed() {
            Throwable exception = getException();
            log.error("Error updating list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка перемещений: " + exception)
                    .showException(exception);
        }
    }

    class RemoveTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private CartSetChange cartSetChange;

        RemoveTask(CartSetChange cartSetChange) {
            this.cartSetChange = cartSetChange;
        }

        @Override
        protected Void call() throws Exception {
            log.debug("Remove CartSetChange...");

            cartSetChangeService.removeChange(cartSetChange);

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.debug("Remove from observableList ...");
            observableChanges.removeAll(cartSetChange);

            log.info("Removing complete.");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error removing CartSetChange: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при удалении перемещения: " + exception)
                    .showException(exception);
        }
    }

    class UpdatePlacesTask extends Task<Collection<Place>> {
        @Override
        protected Collection<Place> call() throws Exception {

            return placeService.getAllPlaces();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            ObservableList<Place> places = FXCollections.observableArrayList();
            places.addAll(getValue());
            places.sort(new PlaceComparator());
            fromPlaceComboBox.getItems().clear();
            fromPlaceComboBox.getItems().add(null);
            fromPlaceComboBox.getItems().addAll(places);
            toPlaceComboBox.getItems().clear();
            toPlaceComboBox.getItems().add(null);
            toPlaceComboBox.getItems().addAll(places);
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error receiving Places: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при чтении данных: " + exception)
                    .showException(exception);

        }
    }


    /// Предикаты для фильтров

    class EmptyPredicate implements Predicate<CartSetChange> {
        @Override
        public boolean test(CartSetChange change) {
            return true;
        }
    }

    class FromDatePredicate implements Predicate<CartSetChange> {
        private Date date;

        FromDatePredicate(Date date) {
            this.date = date;
        }
        @Override
        public boolean test(CartSetChange change) {
            return change.getChangeDate().after(date);
        }
    }

    class ToDatePredicate implements Predicate<CartSetChange> {
        private Date date;

        ToDatePredicate(Date date) {
            this.date = date;
        }
        @Override
        public boolean test(CartSetChange change) {
            return change.getChangeDate().before(date);
        }
    }

    class ActionTypePredicate implements Predicate<CartSetChange> {
        private CartSetChange.Type type;

        ActionTypePredicate(CartSetChange.Type type) {
            this.type = type;
        }
        @Override
        public boolean test(CartSetChange change) {
            return change.getType().equals(type);
        }
    }

    class FromPlacePredicate implements Predicate<CartSetChange> {
        private Place place;

        FromPlacePredicate(Place place) {
            this.place = place;
        }
        @Override
        public boolean test(CartSetChange change) {
            return change.getFromPlace()!=null && change.getFromPlace().equals(place);
        }
    }

    class ToPlacePredicate implements Predicate<CartSetChange> {
        private Place place;

        ToPlacePredicate(Place place) {
            this.place = place;
        }
        @Override
        public boolean test(CartSetChange change) {
            return change.getToPlace()!=null && change.getToPlace().equals(place);
        }
    }


    /// Служебные классы

    // Вспомогательные классы для списка сотрудников

    class ActionTypeListCell extends ListCell<CartSetChange.Type> {
        @Override
        protected void updateItem(CartSetChange.Type item, boolean empty) {
            super.updateItem(item, empty);
            if (item==null || empty) {
                setText(null);
            } else {
                setText(item.getDesc());
            }
        }
    }

    class ActionTypeCellFactory implements Callback<ListView<CartSetChange.Type>, ListCell<CartSetChange.Type>> {
        @Override
        public ListCell<CartSetChange.Type> call(ListView<CartSetChange.Type> param) {
            return new ActionTypeListCell();
        }
    }

    class PlaceListCell extends ListCell<Place> {
        @Override
        protected void updateItem(Place item, boolean empty) {
            super.updateItem(item, empty);
            setText(item==null?null:(item.getCommonName()==null?null:item.getCommonName()));
        }

    }

    class PlaceCellFactory implements Callback<ListView<Place>, ListCell<Place>> {
        @Override
        public ListCell<Place> call(ListView<Place> param) {
            return new PlaceListCell();
        }
    }


}
