package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.database.InicializadorBD;

public class MainApp extends Application {

    private static final int SPLASH_WIDTH = 350;
    private static final int SPLASH_HEIGHT = 350;

    @Override
    public void start(Stage primaryStage) throws Exception {
        showSplash(() -> {
            // Se ejecuta después del splash
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
                    Parent root = loader.load();

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/styles/estilos.css").toExternalForm());
                    primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo1.png")));
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("RODC - Sistema de Gestión de Asistencia y Liquidación de Sueldos");
                    primaryStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void showSplash(Runnable onComplete) {
        Stage splashStage = new Stage(StageStyle.TRANSPARENT);
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo3.png")));
        logo.setFitWidth(SPLASH_WIDTH);
        logo.setPreserveRatio(true);

        StackPane splashLayout = new StackPane(logo);
        splashLayout.setStyle("-fx-background-color: transparent;");
        splashLayout.setAlignment(Pos.CENTER);

        Scene splashScene = new Scene(splashLayout, 500, 500, Color.TRANSPARENT);
        splashScene.setFill(Color.TRANSPARENT);
        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.show();

        // Simula carga (x segundos) y luego cierra splash
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Puedes ajustar el tiempo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                splashStage.close();
                onComplete.run();
            });
        }).start();
    }

    public static void main(String[] args) {
        InicializadorBD.inicializar(); // Inicializa la BD
        launch(args);
    }
}