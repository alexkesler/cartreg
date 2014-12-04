package org.kesler.cartreg.gui.cartsetreestr;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import org.controlsfx.dialog.Dialogs;
import org.kesler.cartreg.domain.CartSet;
import org.kesler.cartreg.domain.CartSetChange;
import org.kesler.cartreg.gui.AbstractController;
import org.kesler.cartreg.service.CartSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Контроллер реестра картриджей
 */
public class CartSetReestrController extends AbstractController {

    @FXML protected TableView<CartSet> cartSetTableView;
    @FXML protected ProgressIndicator updateProgressIndicator;

    @Autowired
    protected CartSetService cartSetService;


    private final ObservableList<CartSet> observableCartSets = FXCollections.observableArrayList();

    @FXML
    protected void initialize() {
        SortedList<CartSet> sortedCartSets = new SortedList<CartSet>(observableCartSets);
//        sortedCartSets.comparatorProperty().bind(cartSetTableView.comparatorProperty());
        sortedCartSets.setComparator(new CartSetComparator());
        cartSetTableView.setItems(sortedCartSets);
    }

    @Override
    public void show(Window owner) {
        observableCartSets.clear();
        super.show(owner, "Реестр картриджей");
    }

    @Override
    protected void updateContent() {

        log.info("Updating List");
        UpdateTask updateListTask = new UpdateTask();

        BooleanBinding runningBinding = updateListTask.stateProperty().isEqualTo(Task.State.RUNNING);
        updateProgressIndicator.visibleProperty().bind(runningBinding);

        observableCartSets.clear();
        new Thread(updateListTask).start();

    }

    // Классы для обновления данных в отдельном потоке

    class UpdateTask extends Task<Collection<CartSet>> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        protected Collection<CartSet> call() throws Exception {
            log.debug("Updating list...");

            Collection<CartSet> cartSets = cartSetService.getAllCartSets();
            log.debug("Server return " + cartSets.size() + " cartSets");
            return cartSets;
        }

        @Override
        protected void succeeded() {
            Collection<CartSet> cartSets = getValue();


            log.debug("Update observableList ...");
            observableCartSets.addAll(cartSets);

            log.info("List update complete.");
        }

        @Override
        protected void failed() {
            Throwable exception = getException();
            log.error("Error updating list: " + exception, exception);
            Dialogs.create()
                    .owner(stage)
                    .title("Ошибка")
                    .message("Ошибка при получении списка: " + exception)
                    .showException(exception);
        }
    }

}
