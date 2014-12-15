package org.kesler.cartreg.gui.cartset;

import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.carttype.CartTypeListController;
import org.kesler.cartreg.service.CartTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;


/**
 * Контроллер для набора картриджей
 */
@Component
public class CartSetController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected ComboBox<CartType> cartTypeComboBox;
    @FXML protected ComboBox<CartStatus> cartStatusComboBox;
    @FXML protected TextField quantityTextField;
    @FXML protected ProgressIndicator updateProgressIndicator;

    @Autowired
    private CartTypeListController cartTypeListController;

    @Autowired
    private CartTypeService cartTypeService;

    @FXML void initialize() {
        cartStatusComboBox.getItems().addAll(CartStatus.values());


        cartStatusComboBox.setButtonCell(new CartStatusListCell());
        cartStatusComboBox.setCellFactory(param -> new CartStatusListCell());

        cartTypeComboBox.setButtonCell(new CartTypeListCell());
        cartTypeComboBox.setCellFactory(param -> new CartTypeListCell());

    }

    private CartSet cartSet;
    private CartType cartType;

    public void show(Window owner) {
        cartSet = new CartSet();
        super.show(owner, "Картриджи");
    }

    public void show(Window owner, CartSet cartSet) {
        this.cartSet = cartSet;
        super.show(owner, "Картриджи");
    }

    public void showAndWait(Window owner) {
        cartSet = new CartSet();
        super.showAndWait(owner, "Картриджи");
    }

    public void showAndWait(Window owner, CartSet cartSet) {
        this.cartSet = cartSet;
        super.showAndWait(owner, "Картриджи");
    }

    @FXML protected void handleSelectCartTypeButtonAction(ActionEvent ev) {
        showSelectCartTypeDialog();
    }

    @Override
    protected void updateResult() {
        cartSet.setType(cartTypeComboBox.getValue());
        cartSet.setStatus(cartStatusComboBox.getValue());
        cartSet.setQuantity(Integer.parseInt(quantityTextField.getText()));
    }

    @Override
    protected void updateContent() {
        cartType = cartSet.getType();
        cartTypeComboBox.setValue(cartType); /// это так устроено странно
        updateCartTypes();
        cartTypeComboBox.setValue(null); // обходной путь чтобы правильно отображалось
        cartStatusComboBox.setValue(cartSet.getStatus());
        quantityTextField.setText(cartSet.getQuantity()==null?"5":cartSet.getQuantity().toString());
        quantityTextField.requestFocus();
        quantityTextField.selectAll();
    }

    private void showSelectCartTypeDialog() {
        cartTypeListController.showAndWaitSelect(stage);
        if (cartTypeListController.getResult()==Result.OK) {
            cartType = cartTypeListController.getSelectedItem();
            updateCartTypes();
            quantityTextField.requestFocus();
            quantityTextField.selectAll();
        }
    }


    private void updateCartTypes() {
        CartTypeUpdater cartTypeUpdater = new CartTypeUpdater();

        BooleanBinding runningBinding = cartTypeUpdater.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        new Thread(cartTypeUpdater).start();
    }



    /// Класс для обновления типов картриджей в отдельном потоке

    class CartTypeUpdater extends Task<Collection<CartType>> {
        @Override
        protected Collection<CartType> call() throws Exception {
            Collection<CartType> cartTypes = cartTypeService.getAllCartTypes();
            return cartTypes;
        }

        @Override
        protected void succeeded() {
            super.succeeded();

            cartTypeComboBox.getItems().clear();
            cartTypeComboBox.getItems().addAll(getValue());
            cartTypeComboBox.setValue(null);  // обходной путь чтобы правильно отображалось
            if (cartType!=null) cartTypeComboBox.setValue(cartType);
            else cartTypeComboBox.getSelectionModel().select(0);
        }

        @Override
        protected void failed() {
            super.failed();
            Throwable exception = getException();
            log.error("Error saving arrival data: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при чтении данных: " + exception)
                    .showException(exception);

        }
    }


    class CartStatusListCell extends ListCell<CartStatus> {
        @Override
        protected void updateItem(CartStatus item, boolean empty) {
            super.updateItem(item, empty);
            if (item==null || empty) {
                setText(null);
            } else {
                setText(item.getDesc());
            }
        }
    }


    class CartTypeListCell extends ListCell<CartType> {
        @Override
        protected void updateItem(CartType item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
            } else {
                setText(item.getModel());
            }
        }

    }

}
