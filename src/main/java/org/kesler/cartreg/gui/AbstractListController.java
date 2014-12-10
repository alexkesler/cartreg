package org.kesler.cartreg.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Контроллер для окна со списком
 */
public abstract class AbstractListController<T> extends AbstractController {
    protected final static Logger log = LoggerFactory.getLogger(AbstractListController.class);

    protected T selectedItem;
    protected boolean select;

    @FXML protected ToolBar okCancelButtonToolBar;
    @FXML protected ToolBar closeButtonToolBar;

    public T getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void show(Window owner) {
        log.info("Show dialog");
        select=false;
        switchToolBar();
        super.show(owner);
    }

   @Override
    public void show(Window owner, String title) {
        log.info("Show dialog with title " + title);
        select=false;
        switchToolBar();
        super.show(owner, title);
    }

   @Override
    public void show(Window owner, String title, Image icon) {
        log.info("Show dialog with title " + title + " and icon");
        select=false;
        switchToolBar();
        super.show(owner, title, icon);
    }

    @Override
    public void showAndWait(Window owner) {
        log.info("Show and wait dialog");
        select=false;
        switchToolBar();
        super.showAndWait(owner);    }

    @Override
    public void showAndWait(Window owner, String title) {
        log.info("Show and wait dialog with title " + title);
        select=false;
        switchToolBar();
        super.showAndWait(owner, title);
    }

    @Override
    public void showAndWait(Window owner, String title, Image icon) {
        log.info("Show and wait dialog with title " + title + " and icon");
        select=false;
        switchToolBar();
        super.showAndWait(owner, title, icon);
    }


    public void showAndWaitSelect(Window owner) {
        log.info("Show and wait select dialog");
        select=true;
        switchToolBar();
        super.showAndWait(owner);
    }

    public void showAndWaitSelect(Window owner, String title) {
        log.info("Show and wait select dialog with title " + title);
        select=true;
        switchToolBar();
        super.showAndWait(owner, title);
    }

    public void showAndWaitSelect(Window owner, String title, Image icon) {
        log.info("Show and wait select dialog with title " + title + " and icon");
        select=true;
        switchToolBar();
        super.showAndWait(owner, title, icon);
    }


    private void switchToolBar () {
        if (okCancelButtonToolBar==null || closeButtonToolBar==null) return;

        if (select) {
            log.debug("Switch ButtonToolbar to Select");
            okCancelButtonToolBar.setVisible(true);
            closeButtonToolBar.setVisible(false);
        } else {
            log.debug("Switch ButtonToolbar to Close");
            okCancelButtonToolBar.setVisible(false);
            closeButtonToolBar.setVisible(true);
        }
    }

}
