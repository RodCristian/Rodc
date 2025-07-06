package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.database.InicializadorBD;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/main/ui/fxml/main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sistema de Gestión");
        primaryStage.show();
    }

    public static void main(String[] args) {
        InicializadorBD.inicializar(); // Si quieres inicializar la BD aquí
        launch(args);
    }
}