package org.kesler.cartreg.gui.carttype;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.Callback;
import org.kesler.cartreg.domain.CartType;
import org.kesler.cartreg.gui.AbsractListController;
import org.kesler.cartreg.service.CartTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Контроллер для управления списком типов картриджей
 */
@Component
public class CartTypeListController extends AbsractListController<CartType> {
    private final Logger log = LoggerFactory.getLogger(CartTypeListController.class);

    @FXML protected ListView<CartType> cartTypeListView;
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
        observableCartTypes.clear();
        observableCartTypes.addAll(cartTypeService.getAllCartTypes());
    }

    @Override
    protected void updateResult() {
        selectedItem = cartTypeListView.getSelectionModel().getSelectedItem();
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
            cartTypeService.addCartType(cartType);
            updateContent();
            cartTypeListView.getSelectionModel().select(cartType);
        }
    }

    private void editCartType() {
        log.info("Edit CartType");
        CartType selectedCartType = cartTypeListView.getSelectionModel().getSelectedItem();
        if (selectedCartType!=null) {
            cartTypeController.showAndWait(stage,selectedCartType);
            if (cartTypeController.getResult()==Result.OK) {
                cartTypeService.updateCartType(selectedCartType);
                updateContent();
                cartTypeListView.getSelectionModel().select(selectedCartType);
            }
        }
    }

    private void removeCartType() {
        log.info("Remove CartType");
        CartType selectedCartType = cartTypeListView.getSelectionModel().getSelectedItem();
        if (selectedCartType!=null) {
            cartTypeService.removeCartType(selectedCartType);
            updateContent();
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
}


