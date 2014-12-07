package org.kesler.cartreg.gui.cartsetchanges;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.service.CartSetChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Контроллер для списка изменений
 */
@Component
public class CartSetChangesController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @FXML protected TableView<CartSetChange> cartSetChangesTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;


    @Autowired
    protected CartSetChangeService cartSetChangeService;

    private final ObservableList<CartSetChange> observableCartSetChanges = FXCollections.observableArrayList();

    @FXML
    protected void initialize() {
        cartSetChangesTableView.setItems(observableCartSetChanges);
    }

    @Override
    public void show(Window owner) {
        super.showFullScreen(owner, "Перемещения");
    }


    @Override
    protected void updateContent() {

        log.info("Updating List");
        UpdateTask updateListTask = new UpdateTask();

        BooleanBinding runningBinding = updateListTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        observableCartSetChanges.clear();
        new Thread(updateListTask).start();

    }


    // Классы для обновления данных в отдельном потоке

    class UpdateTask extends Task<Collection<CartSetChange>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Collection<CartSetChange> call() throws Exception {
            log.debug("Updating list...");

            Collection<CartSetChange> changes = cartSetChangeService.getAllChanges();
            log.debug("Server return " + changes.size() + " changes");
            return changes;
        }

        @Override
        protected void succeeded() {
            Collection<CartSetChange> places = getValue();

            log.debug("Update observableList ...");
            observableCartSetChanges.addAll(places);

            log.info("List update complete.");
        }

        @Override
        protected void failed() {
            Throwable exception = getException();
            log.error("Error updating list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка перемещений: " + exception)
                    .showException(exception);
        }
    }
}
