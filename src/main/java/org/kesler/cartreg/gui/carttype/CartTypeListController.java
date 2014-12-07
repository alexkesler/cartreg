package org.kesler.cartreg.gui.carttype;

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
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.gui.AbsractListController;
import org.kesler.cartreg.service.CartTypeService;
import org.kesler.cartreg.util.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Контроллер для управления списком типов картриджей
 */
@Component
public class CartTypeListController extends AbsractListController<CartType> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected ListView<CartType> cartTypeListView;
    @FXML protected ProgressIndicator updateProgressIndicator;
    private ObservableList<CartType> observableCartTypes;

    @Autowired
    private CartTypeService cartTypeService;

    @Autowired
    private CartTypeController cartTypeController;

    @FXML
    protected void initialize() {
        cartTypeListView.setCellFactory(new Callback<ListView<CartType>, ListCell<CartType>>() {
            @Override
            public ListCell<CartType> call(ListView<CartType> param) {
                return new CartTypeListCell();
            }
        });
        observableCartTypes = FXCollections.observableArrayList();
        cartTypeListView.setItems(observableCartTypes);
    }

    @Override
    public void show(Window owner) {
        log.info("Show dialog");
        String title = "Типы картриджей";
        super.show(owner, title);
    }

    @Override
    public void showAndWait(Window owner) {
        String title = "Типы картриджей";
        super.showAndWait(owner, title);
    }

    @Override
    public void showAndWaitSelect(Window owner) {
        log.info("Show select dialog");
        String title = "Выберите тип картриджей";
        super.showAndWaitSelect(owner, title);
    }

    @Override
    protected void updateContent() {
        UpdateListTask updateListTask = new UpdateListTask();

        BooleanBinding runningBinding = updateListTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        observableCartTypes.clear();
        new Thread(updateListTask).start();

    }

    @Override
    protected void handleOk() {
        selectedItem = cartTypeListView.getSelectionModel().getSelectedItem();

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

    @FXML protected void handleAddCartTypeButtonAction(ActionEvent ev) {
        addCartType();
    }

    @FXML protected void handleEditCartTypeButtonAction(ActionEvent ev) {
        editCartType();
    }

    @FXML protected void handleCartTypeListViewMouseClick(MouseEvent ev) {
        if (ev.getClickCount()==2) {
            if (select) {
                handleOk();
            } else {
                editCartType();
            }
        }

    }

    @FXML protected void handleRemoveCartTypeButtonAction(ActionEvent ev) {
        removeCartType();
    }

    private void addCartType() {
        log.info("Add new CartType");
        CartType cartType = new CartType();
        cartType.setModel("");
        cartTypeController.showAndWait(stage, cartType);
        if (cartTypeController.getResult()==Result.OK) {
            log.info("Saving Place: " + cartType.getModel());
            AddCartTypeTask addCartTypeTask = new AddCartTypeTask(cartType);

            BooleanBinding runningBinding = addCartTypeTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(addCartTypeTask).start();

        }
    }

    private void editCartType() {
        log.info("Edit CartType");
        CartType selectedCartType = cartTypeListView.getSelectionModel().getSelectedItem();
        if (selectedCartType!=null) {
            cartTypeController.showAndWait(stage,selectedCartType);
            if (cartTypeController.getResult()==Result.OK) {
                log.info("Updating cartType: " + selectedCartType.getModel());
                UpdateCartTypeTask updateCartTypeTask = new UpdateCartTypeTask(selectedCartType);
                BooleanBinding runningBinding = updateCartTypeTask.stateProperty().isEqualTo(Task.State.RUNNING);
                updateProgressIndicator.visibleProperty().bind(runningBinding);

                new Thread(updateCartTypeTask).start();
            }
        }
    }

    private void removeCartType() {
        log.info("Remove CartType");
        CartType selectedCartType = cartTypeListView.getSelectionModel().getSelectedItem();
        if (selectedCartType!=null) {
            log.info("Removing cartType: " + selectedCartType.getModel());
            RemoveCartTypeTask removeCartTypeTask = new RemoveCartTypeTask(selectedCartType);
            BooleanBinding runningBinding = removeCartTypeTask.stateProperty().isEqualTo(Task.State.RUNNING);
            updateProgressIndicator.visibleProperty().bind(runningBinding);

            new Thread(removeCartTypeTask).start();

        }
    }

    // Вспомогательные классы для списка сотрудников
    class CartTypeListCell extends ListCell<CartType> {

        @Override
        public void updateItem(CartType item, boolean empty) {
            super.updateItem(item, empty);
            setText(item==null?null:item.getModel());
        }
    }


    /// Классы для обновления и сохранения данных в отдельном потоке

    class UpdateListTask extends Task<Collection<CartType>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Collection<CartType> call() throws Exception {
            log.debug("Updating cartTypes list...");

            Collection<CartType> cartTypes = cartTypeService.getAllCartTypes();
            log.debug("Server return " + cartTypes.size() + " cartTypes");
            return cartTypes;
        }

        @Override
        protected void succeeded() {
            Collection<CartType> cartTypes = getValue();

            log.debug("Update observableCartTypes ...");
            observableCartTypes.addAll(cartTypes);

            log.info("List update complete.");
        }

        @Override
        protected void failed() {
            Throwable exception = getException();
            log.error("Error updating CartType list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка размещений: " + exception)
                    .showException(exception);
        }
    }

    class AddCartTypeTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartType cartType;

        AddCartTypeTask(CartType cartType) {
            this.cartType = cartType;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Adding cartType " + cartType.getModel());
            cartTypeService.addCartType(cartType);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            observableCartTypes.addAll(cartType);

            cartTypeListView.getSelectionModel().select(cartType);
            log.info("Adding cartType complete");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error adding cartType: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при добавлении размещения: " + exception)
                    .showException(exception);
        }
    }

    class UpdateCartTypeTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartType cartType;

        UpdateCartTypeTask(CartType cartType) {
            this.cartType = cartType;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Updating cartType " + cartType.getModel());
            cartTypeService.updateCartType(cartType);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            FXUtils.triggerUpdateListView(cartTypeListView, cartType);
            cartTypeListView.getSelectionModel().select(cartType);
            log.info("Updating cartType complete");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error updating cartType: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при обновлении типа картриджа: " + exception)
                    .showException(exception);
        }
    }

    class RemoveCartTypeTask extends Task<Void> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private final CartType cartType;

        RemoveCartTypeTask(CartType cartType) {
            this.cartType = cartType;
        }
        @Override
        protected Void call() throws Exception {
            log.debug("Removing cartType " + cartType.getModel());
            cartTypeService.removeCartType(cartType);
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();

            log.debug("Removing cartType " + cartType.getModel());
            observableCartTypes.removeAll(cartType);

            log.info("Removing cartType complete");
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error removing cartType: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при удалении типа картриджей: " + exception)
                    .showException(exception);
        }
    }




}


