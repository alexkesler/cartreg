package org.kesler.cartreg;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kesler.cartreg.gui.main.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CartRegApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(CartRegApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        log.info("Starting CartReg application");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CartRegAppFactory.class);
        MainController mainController = context.getBean(MainController.class);
//        mainController.setStage(stage);
//        Scene scene = new Scene(mainController.getRoot(), 700, 500);
//        stage.setScene(scene);
//        stage.setTitle("Учет картриджей");
        Image icon = new Image(this.getClass().getResourceAsStream("/images/Printer.png"));
//
//        stage.getIcons().add(icon);
//        stage.show();
        mainController.showMain(stage,"Учет картриджей",icon);
    }
}
