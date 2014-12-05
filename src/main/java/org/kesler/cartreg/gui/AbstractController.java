package org.kesler.cartreg.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController {
    protected static Logger log = LoggerFactory.getLogger(AbstractController.class);
    @FXML protected Parent root;
    protected Stage stage;
    private Scene scene;

    public enum Result{
        OK,
        CANCEL,
        NONE
    }

    protected Result result = Result.NONE;

    public Result getResult() {
        return result;
    }

    public Parent getRoot() { return root; }

    public void show(Window owner) {
        log.info("Show view");
        initStage(owner);
        updateContent();
        result=Result.NONE;
        stage.show();
    }

    public void show(Window owner, String title) {
        log.info("Show view with title: " + title);
        initStage(owner,title);
        result = Result.NONE;
        updateContent();
        stage.showAndWait();

    }

    public void showFullScreen(Window owner) {
        log.info("Show view");
        initStage(owner);
        stage.setMaximized(true);
        updateContent();
        result=Result.NONE;
        stage.show();
    }

    public void showFullScreen(Window owner, String title) {
        log.info("Show view with title: " + title);
        initStage(owner,title);
        stage.setMaximized(true);
        result = Result.NONE;
        updateContent();
        stage.showAndWait();

    }


    public void showAndWait(Window owner) {
        log.info("Show view and wait");
        initStage(owner);
        result = Result.NONE;
        updateContent();
        stage.showAndWait();
    }


    public void showAndWait(Window owner, String title) {
        log.info("Show view and wait with title: " + title);
        initStage(owner, title);
        result = Result.NONE;
        updateContent();
        stage.showAndWait();
    }
    protected void updateContent() {}

    protected void updateResult() {}


    @FXML protected void handleCloseButtonAction(ActionEvent ev) {
        handleClose();
    }

    @FXML protected void handleOkButtonAction(ActionEvent ev) {
        handleOk();
    }

    @FXML protected void handleCancelButtonAction(ActionEvent ev) {
        handleCancel();
    }

    protected void handleOk() {
        log.info("Ok button pressed - saving and close");
        result = Result.OK;
        updateResult();
        stage.hide();
    }

    protected void handleCancel() {
        log.info("Cancel button pressed - close without saving");
        result = Result.CANCEL;
        stage.hide();

    }

    protected void handleClose() {
        log.info("Close button pressed - hide view");
        result = Result.NONE;
        stage.hide();
    }

    private void initStage(Window owner) {
        if (stage==null || !owner.equals(stage.getOwner())) {
            stage = new Stage();
            stage.initOwner(owner);
            if (scene==null) scene = new Scene(root);
            stage.setScene(scene);
        }
    }

    private void initStage(Window owner, String title) {
        if (stage==null || !owner.equals(stage.getOwner())) {
            stage = new Stage();
            stage.initOwner(owner);
            if (scene==null) scene = new Scene(root);
            stage.setScene(scene);
        }
        stage.setTitle(title);
    }

}
