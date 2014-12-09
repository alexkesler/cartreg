package org.kesler.cartreg.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

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
        stage.show();

    }

    public void show(Window owner, String title, Image icon) {
        log.info("Show view with title " + title + " and icon");
        if (stage==null) {
            stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(new Scene(root));
        }
        stage.setTitle(title);
        stage.getIcons().clear();
        stage.getIcons().addAll(icon);
        stage.show();
        updateContent();
    }


    public void showFullScreen(Window owner) {
        log.info("Show view maximized");
        initStage(owner);
        stage.setMaximized(true);
        updateContent();
        result=Result.NONE;
        stage.show();
    }

    public void showFullScreen(Window owner, String title) {
        log.info("Show view maximized with title: " + title);
        initStage(owner,title);
        stage.setMaximized(true);
        result = Result.NONE;
        updateContent();
        stage.show();

    }

    public void showFullScreen(Window owner, String title, Image icon) {
        log.info("Show view maximized with title: " + title);
        initStage(owner,title);
        stage.setMaximized(true);
        result = Result.NONE;
        stage.getIcons().clear();
        stage.getIcons().addAll(icon);
        updateContent();
        stage.show();

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

    public void showAndWait(Window owner, String title, Image icon) {
        log.info("Show view and wait with title: " + title);
        initStage(owner, title);
        result = Result.NONE;
        stage.getIcons().clear();
        stage.getIcons().addAll(icon);
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
        log.info("Handle Ok action");
        result = Result.OK;
        updateResult();
        hideStage();
    }

    protected void handleCancel() {
        log.info("Handle Cancel action");
        result = Result.CANCEL;
        hideStage();

    }

    protected void handleClose() {
        log.info("Handle Close action");
        result = Result.NONE;
        hideStage();
    }

    protected void hideStage() {
        log.info("Hide stage, result: " + result);
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
