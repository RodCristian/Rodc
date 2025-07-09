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
            Empleado emp = EmpleadoService.buscarEmpleado(seleccionada.getRutEmpleado()); // ✔️ Necesario para nombre y
                                                                                          // cargo

            String folderPath = "Liquidaciones";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists())
                folder.mkdirs();

            String nombreArchivo = "Liquidacion_" + seleccionada.getRutEmpleado() + "_" + seleccionada.getMes()
                    + ".pdf";
            java.io.File file = new java.io.File(folder, nombreArchivo);

            Document document = new Document();
            PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // Título centrado
            Paragraph titulo = new Paragraph("LIQUIDACIÓN DE SUELDO", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" "));

            // Datos personales
            document.add(new Paragraph("Nombre del Trabajador     : " + (emp != null ? emp.getNombre() : "N/A"),
                    normalFont));
            document.add(new Paragraph("RUT                       : " + seleccionada.getRutEmpleado(), normalFont));
            document.add(
                    new Paragraph("Cargo                     : " + (emp != null ? emp.getCargo() : "N/A"), normalFont));
            document.add(new Paragraph("Fecha de Emisión          : " + seleccionada.getFechaGeneracion(), normalFont));
            document.add(new Paragraph("Periodo de Liquidación    : " + seleccionada.getMes(), normalFont));

            document.add(new Paragraph("------------------------------------------------------------", normalFont));
            document.add(new Paragraph("Haberes:", sectionFont));
            document.add(new Paragraph(
                    String.format(" - Sueldo Base            : $%,.0f", seleccionada.getSueldoBase()), normalFont));
            document.add(new Paragraph(
                    String.format(" - Bonificaciones         : $%,.0f", seleccionada.getBonificaciones()), normalFont));
            document.add(new Paragraph(
                    String.format(" - Horas Trabajadas       : %.2f hrs", seleccionada.getTotalHoras()), normalFont));

            document.add(new Paragraph("------------------------------------------------------------", normalFont));
            document.add(new Paragraph("Descuentos:", sectionFont));
            document.add(new Paragraph(
                    String.format(" - Descuentos Aplicados   : $%,.0f", seleccionada.getDescuentos()), normalFont));
            document.add(new Paragraph(" - AFP                    : Monto AFP no disponible", normalFont));
            document.add(new Paragraph(" - Salud (Fonasa/Isapre)  : Monto Salud no disponible", normalFont));
            document.add(new Paragraph(" - Otros                  : Otros descuentos no disponibles", normalFont));

            document.add(new Paragraph("------------------------------------------------------------", normalFont));
            document.add(new Paragraph(
                    String.format("Sueldo Líquido a Pagar    : $%,.0f", seleccionada.getSueldoLiquido()), sectionFont));
            document.add(new Paragraph("------------------------------------------------------------", normalFont));

            document.close();

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "PDF guardado correctamente en la carpeta: " + folder.getAbsolutePath());

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
