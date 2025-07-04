package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.database.InicializadorBD;
import main.service.AsistenciaService;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Sistema de Registro de Asistencia (MVP)");
        Button boton = new Button("Simular escaneo QR");

        boton.setOnAction(e -> {
            AsistenciaService.registrarAsistencia("12345678-9"); // Simulación QR con RUT fijo
        });

        VBox root = new VBox(15, label, boton);
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("RODC - Registro de Asistencia");
        stage.show();
    }

    public static void main(String[] args) {
        InicializadorBD.inicializar();
        launch(args);
    }
}
