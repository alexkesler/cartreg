package org.kesler.cartreg.gui.cartsetreestr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.service.CartSetService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Контроллер реестра картриджей
 */
public class CartSetReestrController extends AbstractController {

    @FXML
    protected TableView<CartSet> cartSetTableView;

    @Autowired
    protected CartSetService cartSetService;


    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();

    @FXML
    protected void initialize() {
        SortedList<CartSet> sortedCartSets = new SortedList<CartSet>(observableCartSets);
//        sortedCartSets.comparatorProperty().bind(cartSetTableView.comparatorProperty());
        sortedCartSets.setComparator(new CartSetComparator());
        cartSetTableView.setItems(sortedCartSets);
    }

    @Override
    public void show(Window owner) {
        observableCartSets.clear();
        super.show(owner, "Реестр картриджей");
    }

    @Override
    protected void updateContent() {
        observableCartSets.clear();
        observableCartSets.addAll(cartSetService.getAllCartSets());
    }
}
