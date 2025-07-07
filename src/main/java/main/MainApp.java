package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.database.InicializadorBD;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // ✅ Carga del archivo main.fxml desde la raíz de resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // ✅ Carga del CSS desde la carpeta /styles
        scene.getStylesheets().add(getClass().getResource("/styles/estilos.css").toExternalForm());

        // (Opcional) Agregar icono/logo si lo deseas en el Stage
        // primaryStage.getIcons().add(new
        // Image(getClass().getResourceAsStream("/images/Logo.png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo.png")));
        primaryStage.setScene(scene);
        primaryStage.setTitle("RODC - Sistema de Gestión de Asistencia y Liquidación de Sueldos");
        primaryStage.show();
    }

    public static void main(String[] args) {
        InicializadorBD.inicializar(); // Inicializa la BD
        launch(args);
    }
}
