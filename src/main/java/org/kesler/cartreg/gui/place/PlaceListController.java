package org.kesler.cartreg.gui.place;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.gui.AbsractListController;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.service.PlaceService;

import org.kesler.cartreg.util.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class PlaceListController extends AbsractListController<Place> {

    protected static Logger log = LoggerFactory.getLogger(PlaceListController.class);

    @Autowired
    private PlaceService placeService;
    private ObservableList<Place> observablePlaces;

    @Autowired
    private PlaceController placeController;

    @FXML protected ListView<Place> placeListView;
    @FXML protected ProgressIndicator updateProgressIndicator;

    private Place.Type[] placeTypes;

    @FXML
    protected void initialize() {
        placeListView.setCellFactory(new Callback<ListView<Place>, ListCell<Place>>() {
            @Override
            public ListCell<Place> call(ListView<Place> param) {
                return new PlaceListCell();
            }
        });
        observablePlaces = FXCollections.observableArrayList();
        placeListView.setItems(observablePlaces);

    }

    @Override
    public void show(Window owner) {
        placeTypes=null;
        super.show(owner,"Размещения");
    }

    @Override
    public void showAndWait(Window owner) {
        placeTypes=null;
        super.showAndWait(owner,"Размещения");
    }

    @Override
    public void showAndWaitSelect(Window owner) {
        placeTypes=null;
        super.showAndWaitSelect(owner, "Выберите размещение");
    }

    public void show(Window owner, Place.Type[] placeTypes) {
        this.placeTypes=placeTypes;
        super.show(owner,"Размещения");
    }

    public void showAndWait(Window owner, Place.Type[] placeTypes) {
        this.placeTypes=placeTypes;
        super.showAndWait(owner,"Размещения");
    }

    public void showAndWaitSelect(Window owner, Place.Type[] placeTypes) {
        this.placeTypes=placeTypes;
        super.showAndWaitSelect(owner, "Выберите размещение");
    }

    @Override
    public void updateContent() {

        log.info("Updating Places List");
        UpdateListTask updateListTask = new UpdateListTask();

        BooleanBinding runningBinding = updateListTask.stateProperty().isEqualTo(Task.State.RUNNING);

        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(updateListTask).start();

    }

    @Override
    protected void updateResult() {
        selectedItem = placeListView.getSelectionModel().getSelectedItem();
    }

    // Обработчики кнопок управления списком сотрудников
    @FXML
    protected void handleAddPlaceButtonAction(ActionEvent ev) {
        addPlace();
    }

    @FXML
    protected void handleEditPlaceButtonAction(ActionEvent ev) {
        editPlace();
    }

    @FXML
    protected void handlePlaceListViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            if (select) {
                handleOk();
            } else {
                editPlace();
            }

        }
    }

    @FXML
    protected void handleRemovePlaceButtonAction(ActionEvent ev) {
        removePlace();
    }



    // Методы управления списком сотрудников

    private void addPlace() {
        log.info("Opening add Place dialog");

        Place newPlace = new Place();
        if (placeTypes!=null) {
            newPlace.setType(placeTypes[0]); // выбираем один из разрешенных
        } else {
            newPlace.setType(Place.Type.BRANCH); // по умолчанию - филиал
        }
        placeController.showAndWait(stage, newPlace, placeTypes);
        if (placeController.getResult() == AbstractController.Result.OK) {
            log.info("Saving Place: " + newPlace.getCommonName());
            AddPlaceTask addPlaceTask = new AddPlaceTask(newPlace);

            BooleanBinding runningBinding = addPlaceTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addPlaceTask).start();
        }

    }

    private void editPlace() {
        Place selectedPlace = placeListView.getSelectionModel().getSelectedItem();
        if (selectedPlace==null) {
            Dialogs.create()
                    .owner(stage)
                    .title("Внимание")
                    .message("Ничего не выбрано")
                    .showWarning();
            return;
        }


        placeController.showAndWait(stage, selectedPlace, placeTypes);
        if (placeController.getResult() == AbstractController.Result.OK) {
            log.info("Updating place: " + selectedPlace.getCommonName());
            UpdatePlaceTask updatePlaceTask = new UpdatePlaceTask(selectedPlace);
            BooleanBinding runningBinding = updatePlaceTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(updatePlaceTask).start();
        }

    }


    private void removePlace() {
        Place selectedPlace = placeListView.getSelectionModel().getSelectedItem();
        if (selectedPlace ==null) {
            Dialogs.create()
                    .owner(root.getScene().getWindow())
                    .title("Внимание")
                    .message("Ничего не выбрано")
                    .showWarning();
            return;
        }

        Action response = Dialogs.create()
                .owner(root.getScene().getWindow())
                .title("Подтверждение")
                .message("Удалить выбранное размещение: " + selectedPlace.getCommonName() + "?")
                .showConfirm();
        if (response == Dialog.ACTION_YES) {
            log.info("Removing place: " + selectedPlace.getCommonName());
            RemovePlaceTask removePlaceTask = new RemovePlaceTask(selectedPlace);
            BooleanBinding runningBinding = removePlaceTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(removePlaceTask).start();
        }
    }


    // Вспомогательные классы для списка сотрудников
    class PlaceListCell extends ListCell<Place> {
        @Override
        protected void updateItem(Place item, boolean empty) {
            super.updateItem(item, empty);
            setText(item==null?null:(item.getCommonName()==null?null:item.getCommonName()));
        }

    }


    // Классы для обновления данных в отдельном потоке

    class UpdateListTask extends Task<Collection<Place>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Collection<Place> call() throws Exception {
            log.debug("Updating places list...");

            Collection<Place> places = placeService.getAllPlaces();
            log.debug("Server return " + places.size() + " places");
            Iterator<Place> placeIterator = places.iterator();
            if (placeTypes!=null) {
                log.debug("Filtering by types " + Arrays.deepToString(placeTypes));
                while (placeIterator.hasNext()) {
                    Place place = placeIterator.next();
                    boolean fit = false;
                    for (Place.Type placeType:placeTypes) {
                        if (place.getType().equals(placeType)) fit=true;
                    }
                    if (!fit) placeIterator.remove();
                }
                log.debug("Filtered " + places.size() + " places");
            }
            return places;
        }

        @Override
        protected void succeeded() {
            Collection<Place> places = getValue();

            log.debug("Update observablePlaces ...");
            observablePlaces.clear();
            observablePlaces.addAll(places);

            log.debug("Sort observablePlaces ...");
            observablePlaces.sort(new PlaceComparator());
            log.info("List update complete.");
        }

        @Override
        protected void failed() {
            Throwable exception = getException();
            log.error("Error updating place list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка размещений: " + exception)
                    .showException(exception);
        }
    }

    class AddPlaceTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final Place newPlace;

        AddPlaceTask(Place newPlace) {
            this.newPlace = newPlace;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Adding place " + newPlace.getCommonName());
            placeService.addPlace(newPlace);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            observablePlaces.add(newPlace);
            observablePlaces.sort(new PlaceComparator());
            placeListView.getSelectionModel().select(newPlace);
            log.info("Adding place complete");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error adding place: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при добавлении размещения: " + exception)
                    .showException(exception);
        }
    }

    class UpdatePlaceTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final Place place;

        UpdatePlaceTask(Place place) {
            this.place = place;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Updating place " + place.getCommonName());
            placeService.updatePlace(place);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            FXUtils.triggerUpdateListView(placeListView, place);
            observablePlaces.sort(new PlaceComparator());
            placeListView.getSelectionModel().select(place);
            log.info("Updating place complete");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error updating place: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при обновлении размещения: " + exception)
                    .showException(exception);
        }
    }

    class RemovePlaceTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final Place place;

        RemovePlaceTask(Place place) {
            this.place = place;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Removing place " + place.getCommonName());
            placeService.removePlace(place);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();

            log.debug("Removing place " + place.getCommonName());
            observablePlaces.removeAll(place);

            log.info("Removing place complete");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error removing place: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при удалении размещения: " + exception)
                    .showException(exception);
        }
    }

 }
