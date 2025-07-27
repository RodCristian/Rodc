package main.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class InventarioController {

    @FXML
    private StackPane contenidoPane;
    @FXML
    private Text txtTitulo;

    @FXML
    public void initialize() {
        txtTitulo.setText("Inventario");
    }

    @FXML
    private void mostrarOficina(ActionEvent event) {
        cargarModulo("/oficina.fxml");
    }

    @FXML
    private void mostrarPlanta(ActionEvent event) {
        cargarModulo("/planta.fxml");
    }

    private void cargarModulo(String fxmlPath) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(fxmlPath));
            contenidoPane.getChildren().setAll(vista); // Reemplaza todo el contenido existente
        } catch (IOException e) {
            System.err.println("Error al cargar el módulo " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            // Aquí puedes mostrar un mensaje de error al usuario, por ejemplo, con un
            // Alert.
        }
    }
}
