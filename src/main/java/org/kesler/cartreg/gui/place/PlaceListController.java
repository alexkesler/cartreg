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
import org.kesler.cartreg.domain.PlaceType;
import org.kesler.cartreg.gui.AbsractListController;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.service.PlaceService;
import org.kesler.cartreg.util.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

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

    private PlaceType[] placeTypes;

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

    public void show(Window owner, PlaceType[] placeTypes) {
        this.placeTypes=placeTypes;
        super.show(owner,"Размещения");
    }

    public void showAndWait(Window owner, PlaceType[] placeTypes) {
        this.placeTypes=placeTypes;
        super.showAndWait(owner,"Размещения");
    }

    public void showAndWaitSelect(Window owner, PlaceType[] placeTypes) {
        this.placeTypes=placeTypes;
        super.showAndWaitSelect(owner, "Выберите размещение");
    }

    @Override
    public void updateContent() {
        observablePlaces.clear();
        Task<Collection<Place>> updateTask = new Task<Collection<Place>>() {
            @Override
            protected Collection<Place> call() throws Exception {
                log.debug("Updating places...");
                return placeService.getAllPlaces();
            }

            @Override
            protected void succeeded() {
                Collection<Place> places = getValue();
                log.debug("Server return " + places.size() + " places");
                if (placeTypes!=null) {
                    log.debug("Filtering by types " + placeTypes);
                    for (Place place:places) {
                        boolean fit = false;
                        for (PlaceType placeType:placeTypes) {
                            if (place.getType().equals(placeType)) fit=true;
                        }
                        if (fit) observablePlaces.addAll(place);
                    }
                    log.debug("Filtered " + observablePlaces.size() + " places");
                } else {
                    observablePlaces.addAll(places);
                }

                observablePlaces.sort(new PlaceComparator());
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                log.error("Error updating: " + exception, exception);
            }
        };

        BooleanBinding runningBinding = updateTask.stateProperty().isEqualTo(Task.State.RUNNING);

        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(updateTask).start();

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
            newPlace.setType(PlaceType.BRANCH); // по умолчанию - филиал
        }
        placeController.showAndWait(stage, newPlace, placeTypes);
        if (placeController.getResult() == AbstractController.Result.OK) {
            log.info("Saving Place: " + newPlace.getCommonName());
            observablePlaces.add(newPlace);
            placeService.addPlace(newPlace);
            observablePlaces.sort(new PlaceComparator());
            placeListView.getSelectionModel().select(newPlace);
        }

    }

    private void editPlace() {
        int selectedIndex = placeListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex<0) {
            Dialogs.create()
                    .owner(root.getScene().getWindow())
                    .title("Внимание")
                    .message("Ничего не выбрано")
                    .showWarning();
            return;
        }

        Place selectedPlace = observablePlaces.get(selectedIndex);

        placeController.showAndWait(stage, selectedPlace, placeTypes);
        if (placeController.getResult() == AbstractController.Result.OK) {
            placeService.updatePlace(selectedPlace);
//            FXUtils.triggerUpdateListView(placeListView, selectedIndex);
            observablePlaces.sort(new PlaceComparator());
            placeListView.getSelectionModel().select(selectedIndex);
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
                .message("Удалить выбранное размещение: " + selectedPlace.getCommonName())
                .showConfirm();
        if (response == Dialog.ACTION_YES) {
            observablePlaces.remove(selectedPlace);
            placeService.removePlace(selectedPlace);
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

 }
