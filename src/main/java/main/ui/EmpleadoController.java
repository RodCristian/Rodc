package main.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.model.Empleado;
import main.service.EmpleadoService;

import java.time.LocalDate;

public class EmpleadoController {

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
    public void initialize() {
        cmbAfp.getItems().addAll("Modelo", "Provida", "Habitat", "Cuprum", "Capital", "PlanVital");
        cmbFonasa.getItems().addAll("Fonasa", "Isapre Colmena", "Isapre CruzBlanca", "Isapre Consalud",
                "Isapre Banmedica", "Isapre Vida Tres", "Isapre Nueva Masvida");
        cmbEstado.getItems().addAll("ACTIVO", "SUSPENDIDO", "INACTIVO");
        cmbEstado.getSelectionModel().selectFirst();
        dpFechaIngreso.setValue(LocalDate.now());
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

            // Verifica si el RUT ya existe
            boolean esNuevo = (EmpleadoService.buscarEmpleado(emp.getRut()) == null);

            EmpleadoService.agregarEmpleado(emp);

            // Solo genera QR si es nuevo
            if (esNuevo) {
                generarYGuardarQR(emp.getRut());
            }

            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    esNuevo ? "Empleado guardado" : "Empleado editado correctamente");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void buscarPorRut() {
        String rut = txtBuscarRut.getText().trim();
        if (!rut.isEmpty()) {
            Empleado emp = EmpleadoService.buscarEmpleado(rut);
            if (emp != null) {
                txtRut.setText(emp.getRut());
                txtNombre.setText(emp.getNombre());
                txtApellido.setText(emp.getApellido());
                dpFechaIngreso.setValue(emp.getFechaIngreso());
                txtCargo.setText(emp.getCargo());
                txtSueldoBase.setText(String.valueOf(emp.getSueldoBase()));
                cmbAfp.setValue(emp.getAfp());
                cmbFonasa.setValue(emp.getFonasa());
                txtCategoria.setText(emp.getCategoria());
                cmbEstado.setValue(emp.getEstado());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado encontrado. Puedes editar o eliminar.");
            } else {
                mostrarAlerta(Alert.AlertType.INFORMATION, "No se encontró el empleado con ese RUT.");
                limpiarCampos();
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Ingresa un RUT para buscar.");
        }
    }

    @FXML
    private void eliminarEmpleado() {
        String rut = txtRut.getText().trim();
        if (!rut.isEmpty()) {
            Empleado emp = EmpleadoService.buscarEmpleado(rut);
            if (emp != null) {
                EmpleadoService.eliminarEmpleado(rut);
                limpiarCampos();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado eliminado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "No se encontró un empleado con ese RUT.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "No hay un RUT para eliminar.");
        }
    }

    @FXML
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
        txtBuscarRut.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void generarYGuardarQR(String rut) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(rut, BarcodeFormat.QR_CODE, 200, 200);
            String folderPath = "QR";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String nombreArchivo = "QR_" + rut + ".png";
            java.io.File file = new java.io.File(folder, nombreArchivo);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", file.toPath());
            mostrarAlerta(Alert.AlertType.INFORMATION, "QR guardado automáticamente en: " + file.getAbsolutePath());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al generar QR: " + e.getMessage());
        }
    }
}