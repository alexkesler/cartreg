package org.kesler.cartreg.gui.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.Place;
import org.kesler.cartreg.domain.PlaceType;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.gui.arrival.ArrivalController;
import org.kesler.cartreg.gui.filing.FilingController;
import org.kesler.cartreg.gui.place.PlaceComparator;
import org.kesler.cartreg.gui.place.PlaceListController;
import org.kesler.cartreg.gui.carttype.CartTypeListController;
import org.kesler.cartreg.gui.move.MoveController;
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
    private MoveController moveController;

    @Autowired
    private FilingController filingController;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private CartSetService cartSetService;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

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

    @FXML protected void handleFillingMenuItemAction(ActionEvent ev) {
        showFillingDialog();
    }

    @FXML protected void handleCloseMenuItemAction(ActionEvent ev) {
        closeApplication();
    }


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
        PlaceType[] placeTypes = {PlaceType.BRANCH};
        placeListController.showAndWaitSelect(stage, placeTypes);
        if(placeListController.getResult()== AbstractController.Result.OK) {
            Place place = placeListController.getSelectedItem();
            if (place!=null) {
                moveController.show(stage,place);
            }
        }
    }

    private void showFillingDialog() {
        log.info("Show FilingDialog");
        filingController.show(stage);
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

    private void reloadTree() {
        List<Place> directs = new ArrayList<Place>(placeService.getDirects());
        directs.sort(new PlaceComparator());
        List<Place> storages = new ArrayList<Place>(placeService.getStorages());
        storages.sort(new PlaceComparator());
        List<Place> branches = new ArrayList<Place>(placeService.getBranches());
        branches.sort(new PlaceComparator());

        TreeItem<TreeObject> rootItem = new TreeItem<TreeObject>(new RootTreeObject());

        TreeItem<TreeObject> directTypeItem = new TreeItem<TreeObject>(new PlaceTypeTreeObject(PlaceType.DIRECT));
        TreeItem<TreeObject> storageTypeItem = new TreeItem<TreeObject>(new PlaceTypeTreeObject(PlaceType.STORAGE));
        TreeItem<TreeObject> branchTypeItem = new TreeItem<TreeObject>(new PlaceTypeTreeObject(PlaceType.BRANCH));

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
        private PlaceType placeType;

        PlaceTypeTreeObject(PlaceType placeType) {
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
