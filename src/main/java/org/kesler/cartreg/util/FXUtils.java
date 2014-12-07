package org.kesler.cartreg.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.*;
import org.kesler.cartreg.domain.CartSet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Универсальные вспомогательные функции для работы с JavaFX
 */
public abstract class FXUtils {

    public static <T> void triggerUpdateListView(ListView<T> listView, int i) {
        EventType<? extends ListView.EditEvent<T>> eventType = ListView.<T>editCommitEvent();
        T newValue = listView.getItems().get(i);
        Event event = new ListView.EditEvent<T>(listView, eventType, newValue, i);
        listView.fireEvent(event);
    }

    public static <T> void triggerUpdateListView(ListView<T> listView, T newValue) {
        EventType<? extends ListView.EditEvent<T>> type = ListView.<T>editCommitEvent();
        int i = listView.getItems().indexOf(newValue);
        Event event = new ListView.EditEvent<T>(listView, type, newValue, i);
        listView.fireEvent(event);
    }

    public static Date localDateToDate(LocalDate localDate) {
        return localDate==null?null:Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date==null?null:date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String getJarPath() {

        URL url = FXUtils.class.getProtectionDomain().getCodeSource().getLocation();
        String jarPath = null;

        try {
            jarPath = URLDecoder.decode(url.getFile(), "UTF-8"); //Should fix it to be read correctly by the system
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String parentPath = new File(jarPath).getParentFile().getPath(); //Path of the jar
        parentPath = parentPath + File.separator;

        return parentPath;

    }

    public static <S> void triggerUpdateTableView(TableView<S> tableView) {
        tableView.getColumns().get(0).setVisible(false);
        tableView.getColumns().get(0).setVisible(true);
    }

    public static <S> void triggerUpdateTableView(TableView<S> tableView, int row) {
        for (TableColumn<S,?> column:tableView.getColumns()) {
            triggerUpdateTableColumn(column, row);
        }
    }

    private static <S, T> void triggerUpdateTableColumn(TableColumn<S, T> column, int row) {

        T value = column.getCellData(row);
        System.out.println("Fire column "+ column.getText() + " changed for row " + row + " value: " + value);
        TablePosition<S,T> position = new TablePosition<S,T>(column.getTableView(),row,column);
        EventType<TableColumn.CellEditEvent<S,T>> eventType = TableColumn.editCommitEvent();
        Event event = new TableColumn.CellEditEvent<S,T>(position.getTableView(), position, eventType, value);
        TableCell<S,T> tableCell = column.getCellFactory().call(column);
        tableCell.fireEvent(event);
//        column.getTableView().fireEvent(event);
    }

    public static <T> void updateObservableList(final ObservableList<T> observableList) {

        List<T> items = new ArrayList<T>(observableList);
        observableList.removeAll(observableList);
        for (T item:items)
            observableList.add(item);

    }

}
