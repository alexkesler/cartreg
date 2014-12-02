package org.kesler.cartreg.gui.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.arrival.ArrivalController;
import org.kesler.cartreg.gui.cartsetchanges.CartSetChangesController;
import org.kesler.cartreg.gui.cartsetreestr.CartSetReestrController;
import org.kesler.cartreg.gui.filling.FillingController;
import org.kesler.cartreg.gui.move.MoveController;
import org.kesler.cartreg.gui.place.PlaceComparator;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.carttype.CartTypeListController;
import org.kesler.cartreg.gui.exchange.ExchangeController;
import org.kesler.cartreg.gui.placecartsets.PlaceCartSetsController;
import org.kesler.cartreg.service.CartSetService;
import org.kesler.cartreg.service.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML protected ProgressIndicator findProgressIndicator;
    @FXML protected Button checkBranchesButton;
    @FXML protected Label taskMessageLabel;
    @FXML protected ProgressIndicator updateProgressIndicator;
    @FXML protected Menu configMenu;
    @FXML protected TreeView<TreeObject> cartTreeView;


    @Autowired
    private ArrivalController arrivalController;

    @Autowired
    private PlaceListController placeListController;

    @Autowired
    private CartTypeListController cartTypeListController;

    @Autowired
    private PlaceCartSetsController placeCartSetsController;

    @Autowired
    private ExchangeController exchangeController;

    @Autowired
    private MoveController moveController;

    @Autowired
    private FillingController fillingController;

    @Autowired
    private CartSetChangesController cartSetChangesController;

    @Autowired
    private CartSetReestrController cartSetReestrController;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private CartSetService cartSetService;


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    // Меню Действия
    @FXML protected void handleConnectMenuItemAction(ActionEvent ev) {

    }

    @FXML protected void handlePlaceCartSetsMenuItemAction(ActionEvent ev) {
        showPlaceCartSetsDialog();
    }

    @FXML protected void handleArrivalMenuItemAction(ActionEvent ev) {
        showArrivalDialog();
    }

    @FXML protected void handleMoveMenuItemAction(ActionEvent ev) {
        showMoveDialog();
    }

    @FXML protected void handleExchangeMenuItemAction(ActionEvent ev) {
        showExchangeDialog();
    }

    @FXML protected void handleFilingMenuItemAction(ActionEvent ev) {
        showFilingDialog();
    }

    @FXML protected void handleWithdrawMenuItemAction(ActionEvent ev) { showWithdrawDialog();}

    @FXML protected void handleCloseMenuItemAction(ActionEvent ev) {
        closeApplication();
    }


    // Меню Отчеты
    @FXML protected void handleCartSetChangesMenuItemAction(ActionEvent ev) {
        showCartSetChangesDialog();
    }

    @FXML protected void handleCartSetReestrMenuItemAction(ActionEvent ev) {
        showCartSetReestrDialog();
    }

    // Меню Настройки
    @FXML protected void handleConectionPropMenuItemAction(ActionEvent ev) {

    }

    @FXML protected void handlePlaceListMenuItemAction(ActionEvent ev) {
        showPlaceListDialog();
    }

    @FXML protected void handleCardTypeListMenuItemAction(ActionEvent ev) {
        showCartTypeListDialog();
    }

    @FXML protected void handleReloadButtonAction(ActionEvent ev) {
        reloadTree();
    }


    // методы для открытия окон
    private void showPlaceCartSetsDialog(){
        log.info("Show PlaceCartSetsDialog...");
        placeListController.showAndWaitSelect(stage);
        if(placeListController.getResult()== AbstractController.Result.OK) {
            Place place = placeListController.getSelectedItem();
            if (place!=null) {
                placeCartSetsController.show(stage,place);
            }
        }
    }

    private void showArrivalDialog() {
        log.info("Show ArrivalDialog");
        arrivalController.show(stage);
    }

    private void showMoveDialog() {
        log.info("Shove MoveDialog");
        moveController.show(stage);
    }

    private void showExchangeDialog() {
        log.info("Shove ExchangeDialog");
        Place.Type[] placeTypes = {Place.Type.BRANCH};
        placeListController.showAndWaitSelect(stage, placeTypes);
        if(placeListController.getResult()== AbstractController.Result.OK) {
            Place place = placeListController.getSelectedItem();
            if (place!=null) {
                exchangeController.show(stage,place);
            }
        }
    }

    private void showFilingDialog() {
        log.info("Show FilingDialog");
        fillingController.show(stage);
    }

    private void showWithdrawDialog() {

    }

    private void showPlaceListDialog() {
        log.info("Show PlaceListDialog");
        placeListController.show(stage);
    }

    private void showCartTypeListDialog() {
        log.info("Show CartTypeListDialog");
        cartTypeListController.show(stage);
    }

    private void closeApplication() {
        log.info("Close Main Window");
        stage.hide();
    }

    private void showCartSetChangesDialog() {
        log.info("Show CartSetChangesDialog");
        cartSetChangesController.show(stage);
    }

    private void showCartSetReestrDialog() {
        log.info("Show CartSetReestrDialog");
        cartSetReestrController.show(stage);
    }

    private void reloadTree() {
        List<Place> directs = new ArrayList<Place>(placeService.getDirects());
        directs.sort(new PlaceComparator());
        List<Place> storages = new ArrayList<Place>(placeService.getStorages());
        storages.sort(new PlaceComparator());
        List<Place> branches = new ArrayList<Place>(placeService.getBranches());
        branches.sort(new PlaceComparator());

        TreeItem<TreeObject> rootItem = new TreeItem<TreeObject>(new RootTreeObject());

        TreeItem<TreeObject> directTypeItem = new TreeItem<TreeObject>(new PlaceTypeTreeObject(Place.Type.DIRECT));
        TreeItem<TreeObject> storageTypeItem = new TreeItem<TreeObject>(new PlaceTypeTreeObject(Place.Type.STORAGE));
        TreeItem<TreeObject> branchTypeItem = new TreeItem<TreeObject>(new PlaceTypeTreeObject(Place.Type.BRANCH));

        rootItem.getChildren().add(directTypeItem);
        rootItem.getChildren().add(storageTypeItem);
        rootItem.getChildren().add(branchTypeItem);

        for (Place direct:directs) {
            TreeItem<TreeObject> directItem = new TreeItem<TreeObject>(new PlaceTreeObject(direct));
            for (CartSet cartSet:cartSetService.getCartSetsByPlace(direct)) {
                directItem.getChildren().add(new TreeItem<TreeObject>(new CartSetTreeObject(cartSet)));
            }
            directItem.setExpanded(true);
            directTypeItem.getChildren().add(directItem);
        }
        directTypeItem.setExpanded(true);

        for (Place storage:storages) {
            TreeItem<TreeObject> storageItem = new TreeItem<TreeObject>(new PlaceTreeObject(storage));
            for (CartSet cartSet:cartSetService.getCartSetsByPlace(storage)) {
                storageItem.getChildren().add(new TreeItem<TreeObject>(new CartSetTreeObject(cartSet)));
            }
            storageItem.setExpanded(true);
            storageTypeItem.getChildren().add(storageItem);
        }
        storageTypeItem.setExpanded(true);

        for (Place branch:branches) {
            TreeItem<TreeObject> branchItem = new TreeItem<TreeObject>(new PlaceTreeObject(branch));
            for (CartSet cartSet:cartSetService.getCartSetsByPlace(branch)) {
                branchItem.getChildren().add(new TreeItem<TreeObject>(new CartSetTreeObject(cartSet)));
            }
            branchTypeItem.getChildren().add(branchItem);
        }
        branchTypeItem.setExpanded(true);

        cartTreeView.setRoot(rootItem);
        cartTreeView.setShowRoot(false);

    }

    abstract class TreeObject {

    }

    class RootTreeObject extends TreeObject {
        @Override
        public String toString() {
            return "Корневой";
        }
    }

    class PlaceTypeTreeObject extends TreeObject {
        private Place.Type placeType;

        PlaceTypeTreeObject(Place.Type placeType) {
            this.placeType = placeType;
        }

        @Override
        public String toString() {
            return placeType.getDesc();
        }
    }

    class PlaceTreeObject extends TreeObject {
        private Place place;
        PlaceTreeObject(Place place) {
            this.place = place;
        }

        @Override
        public String toString() {
            return place.getName();
        }
    }

    class CartSetTreeObject extends TreeObject {
        private CartSet cartSet;
        CartSetTreeObject(CartSet cartSet) {
            this.cartSet = cartSet;
        }

        @Override
        public String toString() {
            return cartSet.getModel() + " (" + cartSet.getStatusDesc() + ") - " + cartSet.getQuantity();
        }
    }

}
