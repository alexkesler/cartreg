package org.kesler.cartreg.gui.cartset;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Callback;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartStatus;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.carttype.CartTypeListController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Контроллер для набора картриджей
 */
@Component
public class CartSetController extends AbstractController {

    @FXML protected Label cartTypeLabel;
    @FXML protected ComboBox<CartStatus> cartStatusComboBox;
    @FXML protected TextField quantityTextField;

    @Autowired
    private CartTypeListController cartTypeListController;

    @FXML void initialize() {
        cartStatusComboBox.getItems().addAll(CartStatus.values());
        cartStatusComboBox.setCellFactory(new Callback<ListView<CartStatus>, ListCell<CartStatus>>() {
            @Override
            public ListCell<CartStatus> call(ListView<CartStatus> param) {
                return new ListCell<CartStatus>() {
                    @Override
                    protected void updateItem(CartStatus item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item==null || empty) {
                            setText(null);
                        } else {
                            setText(item.getDesc());
                        }
                    }
                };
            }
        });
        cartStatusComboBox.setButtonCell(new ListCell<CartStatus>(){
            @Override
            protected void updateItem(CartStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (item==null || empty) {
                    setText(null);
                } else {
                    setText(item.getDesc());
                }
            }
        });
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
        cartSet.setType(cartType);
        cartSet.setStatus(cartStatusComboBox.getValue());
        cartSet.setQuantity(Integer.parseInt(quantityTextField.getText()));
    }

    @Override
    protected void updateContent() {
        cartType = cartSet.getType();
        cartTypeLabel.setText(cartType==null?"Не определен":cartType.getModel());
        cartStatusComboBox.setValue(cartSet.getStatus());
        quantityTextField.setText(cartSet.getQuantity()==null?"5":cartSet.getQuantity().toString());
        quantityTextField.requestFocus();
        quantityTextField.selectAll();
    }

    private void showSelectCartTypeDialog() {
        cartTypeListController.showAndWaitSelect(stage);
        if (cartTypeListController.getResult()==Result.OK) {
            cartType = cartTypeListController.getSelectedItem();
            cartTypeLabel.setText(cartType==null?"Не опеределен":cartType.getModel());
            quantityTextField.requestFocus();
            quantityTextField.selectAll();
        }
    }


}
