package main.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import main.model.Asistencia;
import main.service.AsistenciaService;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AsistenciaController {

    @FXML
    private TextField txtRut;
    @FXML
    private ComboBox<String> cmbTipo;
    @FXML
    private TableView<Asistencia> tablaAsistencias;
    @FXML
    private TableColumn<Asistencia, LocalDate> colFecha;
    @FXML
    private TableColumn<Asistencia, LocalTime> colHora;
    @FXML
    private TableColumn<Asistencia, String> colRut;
    @FXML
    private TableColumn<Asistencia, String> colTipo;

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Entrada1", "Salida Almorzar", "Entrada2", "Salida");
        cmbTipo.getSelectionModel().selectFirst();

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colRut.setCellValueFactory(new PropertyValueFactory<>("rutEmpleado"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        cargarAsistencias();
    }

    @FXML
    private void marcarAsistencia() {
        String rut = txtRut.getText().trim();
        String tipo = cmbTipo.getValue();
        if (rut.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ingrese el RUT.");
            return;
        }
        AsistenciaService.registrarAsistencia(rut, tipo);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Asistencia registrada para RUT: " + rut + " (" + tipo + ")");
        cargarAsistencias();
        txtRut.clear();
    }

    @FXML
    private void marcarAsistenciaQR() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecciona imagen con QR");
            File file = fileChooser.showOpenDialog(txtRut.getScene().getWindow());
            if (file == null)
                return;

            BufferedImage bufferedImage = ImageIO.read(file);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);

            String rut = result.getText();

            // Preguntar tipo de marcaje
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Entrada", "Entrada1", "Salida-Almorzar", "Entrada2",
                    "Salida");
            dialog.setTitle("Tipo de Asistencia");
            dialog.setHeaderText("Selecciona tipo de marcaje");
            dialog.setContentText("Tipo:");
            String tipo = dialog.showAndWait().orElse(null);
            if (tipo == null)
                return;

            AsistenciaService.registrarAsistencia(rut, tipo);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Asistencia registrada para RUT: " + rut + " (" + tipo + ")");
            cargarAsistencias();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al leer QR: " + e.getMessage());
        }
    }

    private void cargarAsistencias() {
        // 1. Traemos todo
        List<Asistencia> lista = AsistenciaService.listarAsistenciasPorEmpleado(null);

        // 2. Mensaje de depuración
        System.out.println("⏺  Registros recibidos: " + lista.size());
        lista.forEach(a -> System.out.println(
                a.getFecha() + " " + a.getHora() + " " + a.getRutEmpleado() + " " + a.getTipo()));

        // 3. Convertimos a ObservableList y la inyectamos
        ObservableList<Asistencia> datos = FXCollections.observableArrayList(lista);
        tablaAsistencias.setItems(datos);

        // 4. Forzamos refresco visual
        tablaAsistencias.refresh();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void refrescarTabla() {
        cargarAsistencias();
    }

}
