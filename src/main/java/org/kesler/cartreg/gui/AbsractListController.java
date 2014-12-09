package org.kesler.cartreg.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.stage.Window;

/**
 * Контроллер для окна со списком
 */
public abstract class AbsractListController<T> extends AbstractController {
    protected T selectedItem;
    protected boolean select;

    @FXML protected ToolBar okCancelButtonToolBar;
    @FXML protected ToolBar closeButtonToolBar;

    public T getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void show(Window owner) {
        select=false;
        switchToolBar();
        super.show(owner);
    }

    @Override
    public void showAndWait(Window owner) {
        select=false;
        switchToolBar();
        super.showAndWait(owner);    }

    public void show(Window owner, String title) {
        select=false;
        switchToolBar();
        super.show(owner, title);
    }

    public void showAndWait(Window owner, String title) {
        select=false;
        switchToolBar();
        super.showAndWait(owner, title);
    }

    public void showAndWait(Window owner, String title, Image icon) {
        select=false;
        switchToolBar();
        super.showAndWait(owner, title, icon);
    }

    public void showAndWaitSelect(Window owner) {
        select=true;
        switchToolBar();
        super.showAndWait(owner);
    }

    public void showAndWaitSelect(Window owner, String title) {
        select=true;
        switchToolBar();
        super.showAndWait(owner, title);
    }

    public void showAndWaitSelect(Window owner, String title, Image icon) {
        select=true;
        switchToolBar();
        super.showAndWait(owner, title, icon);
    }


    private void switchToolBar () {
        if (okCancelButtonToolBar==null || closeButtonToolBar==null) return;

        if (select) {
            okCancelButtonToolBar.setVisible(true);
            closeButtonToolBar.setVisible(false);
        } else {
            okCancelButtonToolBar.setVisible(false);
            closeButtonToolBar.setVisible(true);
        }
    }

}
