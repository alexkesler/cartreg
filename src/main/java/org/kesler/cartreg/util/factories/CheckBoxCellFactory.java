package org.kesler.cartreg.util.factories;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * Created by alex on 13.12.14.
 */
public class CheckBoxCellFactory<S,T> implements Callback<TableColumn<S,T>,TableCell<S,T>> {
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new CheckBoxTableCell<S, T>();
    }
}
