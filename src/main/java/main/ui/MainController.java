package main.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class MainController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private AnchorPane contenidoPane;

    @FXML
    private void mostrarEmpleados() throws Exception {
        Parent vista = FXMLLoader.load(getClass().getResource("/main/ui/fxml/empleado.fxml"));
        contenidoPane.getChildren().setAll(vista);
    }

    @FXML
    private void mostrarLiquidaciones() throws Exception {
        Parent vista = FXMLLoader.load(getClass().getResource("/main/ui/fxml/liquidacion.fxml"));
        contenidoPane.getChildren().setAll(vista);
    }

    @FXML
    private void mostrarAsistencia() throws Exception {
        Parent vista = FXMLLoader.load(getClass().getResource("/main/ui/fxml/asistencia.fxml"));
        contenidoPane.getChildren().setAll(vista);
    }
}
