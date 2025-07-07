package main.ui;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.Empleado;
import main.model.Liquidacion;
import main.service.EmpleadoService;
import main.service.LiquidacionService;

import java.time.LocalDate;
import java.util.List;

public class LiquidacionController {

    @FXML
    private ComboBox<String> cmbEmpleado;
    @FXML
    private TextField txtMes;
    @FXML
    private TextField txtDescuentos;
    @FXML
    private TableView<Liquidacion> tablaLiquidaciones;
    @FXML
    private TableColumn<Liquidacion, String> colMes;
    @FXML
    private TableColumn<Liquidacion, Double> colSueldoBase;
    @FXML
    private TableColumn<Liquidacion, Double> colTotalHoras;
    @FXML
    private TableColumn<Liquidacion, Double> colBonificaciones;
    @FXML
    private TableColumn<Liquidacion, Double> colDescuentos;
    @FXML
    private TableColumn<Liquidacion, Double> colSueldoLiquido;
    @FXML
    private TableColumn<Liquidacion, LocalDate> colFechaGeneracion;

    @FXML
    public void initialize() {
        // Cargar empleados en el ComboBox (puedes mostrar rut o nombre)
        List<Empleado> empleados = EmpleadoService.obtenerEmpleados();
        for (Empleado emp : empleados) {
            cmbEmpleado.getItems().add(emp.getRut());
        }

        // Configurar columnas de la tabla
        colMes.setCellValueFactory(new PropertyValueFactory<>("mes"));
        colSueldoBase.setCellValueFactory(new PropertyValueFactory<>("sueldoBase"));
        colTotalHoras.setCellValueFactory(new PropertyValueFactory<>("totalHoras"));
        colBonificaciones.setCellValueFactory(new PropertyValueFactory<>("bonificaciones"));
        colDescuentos.setCellValueFactory(new PropertyValueFactory<>("descuentos"));
        colSueldoLiquido.setCellValueFactory(new PropertyValueFactory<>("sueldoLiquido"));
        colFechaGeneracion.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
    }

    @FXML
    private void generarLiquidacion() {
        String rut = cmbEmpleado.getValue();
        String mes = txtMes.getText().trim();
        double descuentos = 0.0;
        try {
            descuentos = Double.parseDouble(txtDescuentos.getText().trim());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Descuentos inválidos.");
            return;
        }

        if (rut == null || mes.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona empleado y mes.");
            return;
        }

        LiquidacionService.generarLiquidacion(rut, mes, descuentos);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Liquidación generada.");
        cargarLiquidaciones(rut);
    }

    private void cargarLiquidaciones(String rut) {
        List<Liquidacion> lista = LiquidacionService.listarLiquidaciones(rut);
        tablaLiquidaciones.getItems().setAll(lista);
    }

    @FXML
    private void exportarPDF() {
        Liquidacion seleccionada = tablaLiquidaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una liquidación para exportar.");
            return;
        }

        try {
            // Carpeta predeterminada "pdfs"
            String folderPath = "Liquidaciones";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // crea la carpeta si no existe
            }

            // Nombre de archivo único por ID o fecha
            String nombreArchivo = "Liquidacion_" + seleccionada.getRutEmpleado() + ".pdf";
            java.io.File file = new java.io.File(folder, nombreArchivo);

            // Crear y escribir el PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            document.add(new Paragraph("Liquidación de Sueldo"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Empleado: " + cmbEmpleado.getValue()));
            document.add(new Paragraph("Mes: " + seleccionada.getMes()));
            document.add(new Paragraph("Sueldo Base: " + seleccionada.getSueldoBase()));
            document.add(new Paragraph("Total Horas: " + seleccionada.getTotalHoras()));
            document.add(new Paragraph("Bonificaciones: " + seleccionada.getBonificaciones()));
            document.add(new Paragraph("Descuentos: " + seleccionada.getDescuentos()));
            document.add(new Paragraph("Sueldo Líquido: " + seleccionada.getSueldoLiquido()));
            document.add(new Paragraph("Fecha Generación: " + seleccionada.getFechaGeneracion()));

            document.close();

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "PDF guardado automáticamente en la carpeta: " + folder.getAbsolutePath());

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al generar PDF: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
