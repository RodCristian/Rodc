package main.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import main.model.Empleado;
import main.service.EmpleadoService;

import java.time.LocalDate;
import java.util.List;

public class EmpleadoController {

    // Campos del formulario
    @FXML
    private TextField txtRut;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private DatePicker dpFechaIngreso;
    @FXML
    private TextField txtCargo;
    @FXML
    private TextField txtSueldoBase;
    @FXML
    private ComboBox<String> cmbAfp;
    @FXML
    private ComboBox<String> cmbFonasa;
    @FXML
    private TextField txtCategoria;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TextField txtBuscarRut;

    @FXML
    private TableView<Empleado> tablaEmpleados;
    @FXML
    private TableColumn<Empleado, String> colRut;
    @FXML
    private TableColumn<Empleado, String> colNombre;
    @FXML
    private TableColumn<Empleado, String> colApellido;
    @FXML
    private TableColumn<Empleado, String> colFechaIngreso;
    @FXML
    private TableColumn<Empleado, String> colCargo;
    @FXML
    private TableColumn<Empleado, Double> colSueldoBase;
    @FXML
    private TableColumn<Empleado, String> colAfp;
    @FXML
    private TableColumn<Empleado, String> colFonasa;
    @FXML
    private TableColumn<Empleado, String> colCategoria;
    @FXML
    private TableColumn<Empleado, String> colEstado;

    @FXML
    public void initialize() {
        // Popular combos (puedes cargar desde BDs o archivos si prefieres)
        cmbAfp.getItems().addAll("Modelo", "Provida", "Habitat", "Cuprum", "Capital", "PlanVital");
        cmbFonasa.getItems().addAll("Fonasa", "Isapre Colmena", "Isapre CruzBlanca", "Isapre Consalud",
                "Isapre Banmedica", "Isapre Vida Tres", "Isapre Nueva Masvida");
        cmbEstado.getItems().addAll("ACTIVO", "SUSPENDIDO", "INACTIVO");
        // Valor por defecto
        cmbEstado.getSelectionModel().selectFirst();
        dpFechaIngreso.setValue(LocalDate.now());

        colRut.setCellValueFactory(new PropertyValueFactory<>("rut"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colFechaIngreso.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colSueldoBase.setCellValueFactory(new PropertyValueFactory<>("sueldoBase"));
        colAfp.setCellValueFactory(new PropertyValueFactory<>("afp"));
        colFonasa.setCellValueFactory(new PropertyValueFactory<>("fonasa"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarEmpleados();
    }

    @FXML
    private void guardarEmpleado() {
        try {
            Empleado emp = new Empleado();
            emp.setRut(txtRut.getText().trim());
            emp.setNombre(txtNombre.getText().trim());
            emp.setApellido(txtApellido.getText().trim());
            emp.setFechaIngreso(dpFechaIngreso.getValue());
            emp.setCargo(txtCargo.getText().trim());
            emp.setSueldoBase(Double.parseDouble(txtSueldoBase.getText().trim()));
            emp.setAfp(cmbAfp.getValue());
            emp.setFonasa(cmbFonasa.getValue());
            emp.setCategoria(txtCategoria.getText().trim());
            emp.setEstado(cmbEstado.getValue());

            EmpleadoService.agregarEmpleado(emp);
            generarYGuardarQR(emp.getRut());
            // Limpia el formulario y confirma
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado guardado");

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtRut.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtCargo.clear();
        txtSueldoBase.clear();
        txtCategoria.clear();
        dpFechaIngreso.setValue(LocalDate.now());
        cmbAfp.getSelectionModel().clearSelection();
        cmbFonasa.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().selectFirst();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarEmpleados() {
        List<Empleado> empleados = EmpleadoService.obtenerEmpleados();
        tablaEmpleados.getItems().setAll(empleados);
    }

    @FXML
    private void refrescarTabla() {
        cargarEmpleados();
    }

    @FXML
    private void eliminarEmpleado() {
        Empleado seleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            EmpleadoService.eliminarEmpleado(seleccionado.getRut());
            cargarEmpleados();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado eliminado correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un empleado para eliminar.");
        }
    }

    @FXML
    private void buscarPorRut() {
        String rut = txtBuscarRut.getText().trim();
        if (!rut.isEmpty()) {
            Empleado emp = EmpleadoService.buscarEmpleado(rut);
            if (emp != null) {
                tablaEmpleados.getItems().setAll(emp);
            } else {
                mostrarAlerta(Alert.AlertType.INFORMATION, "No se encontró el empleado con ese RUT.");
            }
        } else {
            cargarEmpleados();
        }
    }

    private void generarYGuardarQR(String rut) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(rut, BarcodeFormat.QR_CODE, 200, 200);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar QR del empleado");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG", "*.png"));
            // Usar la ventana principal de la tabla para mostrar el diálogo
            java.io.File file = fileChooser.showSaveDialog(tablaEmpleados.getScene().getWindow());
            if (file != null) {
                MatrixToImageWriter.writeToPath(bitMatrix, "PNG", file.toPath());
                mostrarAlerta(Alert.AlertType.INFORMATION, "QR generado y guardado correctamente.");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al generar QR: " + e.getMessage());
        }
    }
}