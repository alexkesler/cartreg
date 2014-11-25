package org.kesler.cartreg.gui.carttype;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.gui.AbstractController;

/**
 * Контроллер редактирования типа картриджа
 */
public class CartTypeController extends AbstractController {

    @FXML protected TextField cartTypeTextField;

    private CartType cartType;

    public void showAndWait(Window owner, CartType cartType) {
        this.cartType = cartType;
        super.showAndWait(owner, "Тип картриджа");
    }

    @Override
    protected void updateContent() {
        cartTypeTextField.setText(cartType.getModel());
    }

    @Override
    protected void updateResult() {
        cartType.setModel(cartTypeTextField.getText());
    }
}
