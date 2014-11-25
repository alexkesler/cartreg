package org.kesler.cartreg.gui.filing;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.gui.AbstractController;

/**
 * Контроллер для окна заправки
 */
public class FilingController extends AbstractController {

    @FXML protected TableView<CartSet> emptyCartSetsTableView;
    @FXML protected TableView<CartSet> filedCartSetsTableView;
    @FXML protected TableView<CartSet>  defectCartSetsTableView;

    @Override
    public void show(Window owner) {
        super.show(owner,"Заправка картриджей");
    }

    @FXML
    protected void handleAddEmptyCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleEditEmptyCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleEmptyCartSetsTableViewMouceClick(MouseEvent ev) {

    }

    @FXML
    protected void handleRemoveEmptyCartSetButtonAction(ActionEvent ev) {

    }


    @FXML
    protected void handleAddFiledCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleEditFiledCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleFiledCartSetsTableViewMouceClick(MouseEvent ev) {

    }

    @FXML
    protected void handleRemoveFiledCartSetButtonAction(ActionEvent ev) {

    }


    @FXML
    protected void handleAddDefectCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleEditDefectCartSetButtonAction(ActionEvent ev) {

    }

    @FXML
    protected void handleDefectCartSetsTableViewMouceClick(MouseEvent ev) {

    }

    @FXML
    protected void handleRemoveDefectCartSetButtonAction(ActionEvent ev) {

    }
}
