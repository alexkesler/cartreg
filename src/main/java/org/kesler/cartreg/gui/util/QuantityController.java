package org.kesler.cartreg.gui.util;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.gui.AbstractController;

/**
 * Контроллер окна по управлению количеством
 */
public class QuantityController extends AbstractController {
    @FXML protected TextField quantityTextField;
    @FXML protected Label maxQuantityLabel;

    private Integer quantity;

    private Integer maxQuantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void showAndWait(Window owner, Integer quantity, Integer maxQuantity) {
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
        super.showAndWait(owner, "Количество");
    }

    @Override
    protected void updateContent() {
        quantityTextField.setText(quantity==null?"1":quantity.toString());
        maxQuantityLabel.setText(maxQuantity==null?"":maxQuantity.toString());
        quantityTextField.requestFocus();
        quantityTextField.selectAll();
    }

    @Override
    protected void handleOk() {
        quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity > maxQuantity) {
            Dialogs.create()
                    .owner(stage)
                    .title("Внимание")
                    .message("Количество не может превышать " + maxQuantity)
                    .showWarning();
            return;
        }
        result = Result.OK;
        stage.hide();
    }

}
