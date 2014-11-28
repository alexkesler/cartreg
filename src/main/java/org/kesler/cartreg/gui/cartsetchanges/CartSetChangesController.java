package org.kesler.cartreg.gui.cartsetchanges;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.service.CartSetChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Контроллер для списка изменений
 */
@Component
public class CartSetChangesController extends AbstractController {

    @FXML
    protected TableView<CartSetChange> cartSetChangesTableView;


    @Autowired
    protected CartSetChangeService cartSetChangeService;

    private final ObservableList<CartSetChange> observableCartSetChanges = FXCollections.observableArrayList();

    @FXML
    protected void initialize() {
        cartSetChangesTableView.setItems(observableCartSetChanges);
    }

    @Override
    public void show(Window owner) {
        super.show(owner, "Перемещения");
    }


    @Override
    protected void updateContent() {
        observableCartSetChanges.clear();
        observableCartSetChanges.addAll(cartSetChangeService.getAllChanges());
    }
}
