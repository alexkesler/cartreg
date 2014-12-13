package org.kesler.cartreg.gui.placecartsets;

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
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.*;
import org.kesler.cartreg.gui.AbstractListController;
import org.kesler.cartreg.gui.cartset.CartSetController;
import org.kesler.cartreg.gui.cartsetreestr.CartSetComparator;
import org.kesler.cartreg.service.CartSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Контроллер окна наличия картриджей
 */
@Component
public class PlaceCartSetsController extends AbstractListController<CartSet> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected Label placeLabel;
    @FXML protected TableView<CartSet> cartSetsTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;


    @Autowired
    protected CartSetController cartSetController;

    @Autowired
    protected CartSetService cartSetService;

    private Place place;
    private CartStatus[] statuses;

    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();

    @FXML protected void initialize() {
        SortedList<CartSet> sortedCartSets = new SortedList<CartSet>(observableCartSets);
        sortedCartSets.setComparator(new CartSetComparator());
        cartSetsTableView.setItems(sortedCartSets);
    }

    public void show(Window owner, Place place) {
        log.info("Show for place " + place.getCommonName());
        this.place = place;
        statuses = null;
        Image icon = new Image(this.getClass().getResourceAsStream("/images/preferences.png"));

        super.show(owner, "Наличие картриджей", icon);
    }

    public void showAndWait(Window owner, Place place) {
        log.info("Show for place " + place.getCommonName());
        this.place = place;
        statuses = null;
        Image icon = new Image(this.getClass().getResourceAsStream("/images/preferences.png"));

        super.showAndWait(owner, "Наличие картриджей", icon);
    }

    public void showAndWaitSelect(Window owner, Place place) {
        log.info("Show and wait select for place " + place.getCommonName());
        this.place = place;
        statuses = null;
        Image icon = new Image(this.getClass().getResourceAsStream("/images/preferences.png"));

        super.showAndWaitSelect(owner, "Наличие картриджей", icon);
    }

    public void showAndWaitSelect(Window owner, Place place, CartStatus[] statuses) {
        log.info("Show and wait select for place " + place.getCommonName() + " and statuses " + Arrays.deepToString(statuses));
        this.place = place;
        this.statuses = statuses;
        Image icon = new Image(this.getClass().getResourceAsStream("/images/preferences.png"));

        super.showAndWaitSelect(owner, "Наличие картриджей", icon);
    }

    @FXML protected void handleAddButtonAction(ActionEvent ev) {
        addCartSet();
    }

    @FXML protected void handleEditButtonAction(ActionEvent ev) {
        editCartSet();
    }

    @FXML protected void handleCartSetsTableViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            if (select) {
                handleOk();
            } else {
                editCartSet();
            }
        }
    }

    @FXML protected void handleRemoveButtonAction(ActionEvent ev) {
        removeCartSet();
    }

    @Override
    protected void updateContent() {
        if (place==null) {
            placeLabel.setText("Не определено");
            return;
        }
        placeLabel.setText(place.getCommonName());

        log.info("Update CartSets list...");
        UpdateListTask updateListTask = new UpdateListTask();
        BooleanBinding runningBinding = updateListTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        observableCartSets.clear();
        new Thread(updateListTask).start();


    }

    @Override
    protected void handleOk() {
        selectedItem = cartSetsTableView.getSelectionModel().getSelectedItem();

        if (select && selectedItem == null) {
            Dialogs.create()
                    .owner(stage)
                    .title("Внимание")
                    .message("Ничего не выбрано")
                    .showWarning();
        } else {
            result = Result.OK;
            stage.hide();
        }

    }

    private void addCartSet() {
        CartSet cartSet = new CartSet();
        cartSet.setPlace(place);
        // определяем тип по умолчанию в зависимости от расположения
        switch (place.getType()) {
            case STORAGE:
                cartSet.setStatus(CartStatus.NEW);
                break;
            case DIRECT:
                cartSet.setStatus(CartStatus.FILLED);
                break;
            case BRANCH:
                cartSet.setStatus(CartStatus.INSTALLED);
                break;
            default:
                cartSet.setStatus(CartStatus.NONE);
        }

        cartSetController.showAndWait(stage, cartSet);
        if (cartSetController.getResult()==Result.OK) {
            log.info("Adding CartSet...");
            AddCartSetTask addCartSetTask = new AddCartSetTask(cartSet);
            BooleanBinding runningBinding = addCartSetTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addCartSetTask).start();

        }
    }

    private void editCartSet() {
        CartSet selectedCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            cartSetController.showAndWait(stage,selectedCartSet);
            if (cartSetController.getResult()==Result.OK) {
                log.info("Updating CartSet...");
                UpdateCartsetTask updateCartsetTask = new UpdateCartsetTask(selectedCartSet);
                BooleanBinding runningBinding = updateCartsetTask.stateProperty().isEqualTo(Task.State.RUNNING);
                updateProgressIndicator.visibleProperty().bind(runningBinding);

                new Thread(updateCartsetTask).start();
            }

        }
    }

    private void removeCartSet() {
        CartSet selectedCartSet = cartSetsTableView.getSelectionModel().getSelectedItem();
        if (selectedCartSet!=null) {
            log.info("Removing CartSet...");
            RemoveTask removeTask = new RemoveTask(selectedCartSet);
            BooleanBinding runningBinding = removeTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(removeTask).start();
        }
    }


    // Классы для обновления данных в отдельном потоке

    class UpdateListTask extends Task<Collection<CartSet>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Collection<CartSet> call() throws Exception {
            log.debug("Reading CartSets for place: " + place.getCommonName());
            Collection<CartSet> cartSets = cartSetService.getCartSetsByPlace(place);
            log.debug("Server returned " + cartSets.size() + " CartSets");

            log.debug("Filtering CartSets by statuses " + Arrays.deepToString(statuses));
            if (statuses!=null) {
                Iterator<CartSet> cartSetIterator = cartSets.iterator();
                CartSet cartSet;
                while (cartSetIterator.hasNext()) {
                    cartSet = cartSetIterator.next();
                    boolean fit = false;
                    for (CartStatus status:statuses) {
                        if (status.equals(cartSet.getStatus())) {
                            fit=true;
                            break;
                        }
                    }
                    if (!fit) cartSetIterator.remove();
                }
            }


            return cartSets;
        }

        @Override
        protected void succeeded() {
            super.succeeded();

            log.debug("Update list...");
            Collection<CartSet> cartSets = getValue();
            observableCartSets.addAll(cartSets);
            log.info("Update CartSets complete");

        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error update CartSets list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при обновлении списка: " + exception)
                    .showException(exception);
        }
    }

    class AddCartSetTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
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

    class UpdateCartsetTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartSet cartSet;

        UpdateCartsetTask(CartSet cartSet) {
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

    class RemoveTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartSet cartSet;

        RemoveTask(CartSet cartSet) {
            this.cartSet = cartSet;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Removing CartSet...");

            cartSetService.removeCartSet(cartSet);

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log.info("Removing CartSet complete");
            updateContent();
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error removing CartSet: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при удалении набора картриджей: " + exception)
                    .showException(exception);
        }
    }

}
