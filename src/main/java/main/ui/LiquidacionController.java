package main.ui;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.Empleado;
import main.model.Liquidacion;
import main.service.EmpleadoService;
import main.service.LiquidacionService;

import java.io.File;
import java.io.FileOutputStream;
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
        try {
            String folderPath = "Liquidaciones";
            File folder = new File(folderPath);
            if (!folder.exists())
                folder.mkdirs();

            String nombreArchivo = "Liquidacion_Ejemplo_Ajustado.pdf";
            File file = new File(folder, nombreArchivo);

            // Márgenes reducidos (izq, der, sup, inf)
            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Tamaños de fuente ajustados
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f);

            // Encabezado empresa
            document.add(new Paragraph("INDHERCO LTDA.", boldFont));
            document.add(new Paragraph("RUT.: 84.369.200-1", normalFont));
            document.add(new Paragraph("CAMINO INTERNACIONAL 2499", normalFont));
            document.add(new Paragraph("VIÑA DEL MAR - VIÑA DEL MAR", normalFont));
            document.add(new Paragraph(" ", normalFont));

            // Título y mes
            Paragraph titulo = new Paragraph("LIQUIDACION DE SUELDO", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            PdfPTable fecha = new PdfPTable(3);
            fecha.setWidthPercentage(100);
            fecha.setWidths(new float[] { 3, 1, 1 });
            for (String text : new String[] { "", "ABRIL", "DE 2025" }) {
                PdfPCell c = createCell(text, boldFont, Element.ALIGN_CENTER);
                fecha.addCell(c);
            }
            document.add(fecha);
            document.add(new Paragraph(" ", normalFont));

            // Datos personales
            PdfPTable datos = new PdfPTable(4);
            datos.setWidthPercentage(100);
            datos.setWidths(new float[] { 3.2f, 2, 2, 1.5f });
            String[] fila1 = { "NOMBRE :  RODRIGUEZ PEREZ CRISTIAN EDUAR", "AYUDANTE", "", "C.COSTO: 0" };
            String[] fila2 = { "RUT :  19.613.150-7", "CODIGO: 103", "TIPO TRAB.: 1", "" };

            for (String text : fila1)
                datos.addCell(createCell(text, normalFont, Element.ALIGN_LEFT));
            for (String text : fila2)
                datos.addCell(createCell(text, normalFont, Element.ALIGN_LEFT));
            document.add(datos);
            document.add(new Paragraph(" ", normalFont));

            // Haberes imponibles
            PdfPTable haberes = new PdfPTable(3);
            haberes.setWidthPercentage(100);
            haberes.setWidths(new float[] { 4.5f, 1, 2 });
            String[][] dataHaberes = {
                    { "DIAS TRAB. / SUELDO BASE", "26", "442,551" },
                    { "GRATIFICACION", "", "1,099,526" },
                    { "DIAS DE LICENCIA", "3", "" },
                    { "INCENTIVO", "", "132,978" },
                    { "BASE IMPONIBLE", "", "1,675,055" },
            };
            for (String[] fila : dataHaberes)
                for (int i = 0; i < fila.length; i++)
                    haberes.addCell(createCell(fila[i], normalFont, i == 2 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT));
            document.add(haberes);
            document.add(new Paragraph(" ", normalFont));

            // No imponibles
            PdfPTable noImp = new PdfPTable(2);
            noImp.setWidthPercentage(100);
            noImp.setWidths(new float[] { 5, 2 });
            String[][] dataNoImp = {
                    { "ASIG. FAMILIAR", "13,036" },
                    { "MOVILIZACION", "44,200" },
                    { "COLACION", "27,200" },
            };
            for (String[] fila : dataNoImp)
                for (int i = 0; i < fila.length; i++)
                    noImp.addCell(createCell(fila[i], normalFont, i == 1 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT));

            noImp.addCell(createCell("TOTAL NO IMPONIBLE", boldFont, Element.ALIGN_LEFT));
            noImp.addCell(createCell("84,436", boldFont, Element.ALIGN_RIGHT));
            document.add(noImp);

            // Total ganado
            PdfPTable totalGanado = new PdfPTable(2);
            totalGanado.setWidthPercentage(100);
            totalGanado.addCell(createCell("TOTAL GANADO", boldFont, Element.ALIGN_LEFT));
            totalGanado.addCell(createCell("1,759,491", boldFont, Element.ALIGN_RIGHT));
            document.add(totalGanado);
            document.add(new Paragraph(" ", normalFont));

            // Descuentos
            PdfPTable desc = new PdfPTable(6);
            desc.setWidthPercentage(100);
            desc.setWidths(new float[] { 3, 1.2f, 1, 1, 1, 1.2f });
            String[][] dataDesc = {
                    { "INS. PREVISION / Tasa", "PLANVITAL", "11.16", "", "", "" },
                    { "SEGURO DE CESANTIA", "", "0.60", "", "", "" },
                    { "INS. SALUD", "FONASA", "", "", "", "" },
                    { "% COTIZ. SALUD", "", "1.80", "", "", "" },
                    { "CAJA DE COMPENSACION", "", "5.20", "", "", "" },
                    { "AFP+Apv+Seg+Salud=PREVISION", "186,936", "0", "10,050", "30,151", "87,103" },
                    { "REM. AFECTA / TASA IMPTO.", "1,360,815", "", "", "", "0.040" },
                    { "IMPUESTO UNICO", "", "", "", "", "17,547" },
                    { "ANT. GRAT.", "", "", "", "", "820,000" },
                    { "SEG.CCHC", "", "", "", "", "16,000" },
            };
            for (String[] fila : dataDesc)
                for (String celda : fila)
                    desc.addCell(createCell(celda, normalFont, Element.ALIGN_RIGHT));
            document.add(desc);

            // Totales
            document.add(new Paragraph(" ", normalFont));
            PdfPTable totalDesc = new PdfPTable(2);
            totalDesc.setWidthPercentage(100);
            totalDesc.addCell(createCell("TOTAL DESCUENTOS", boldFont, Element.ALIGN_LEFT));
            totalDesc.addCell(createCell("1,167,787", boldFont, Element.ALIGN_RIGHT));
            document.add(totalDesc);

            PdfPTable neto = new PdfPTable(2);
            neto.setWidthPercentage(100);
            neto.addCell(createCell("ALCANCE LIQUIDO", boldFont, Element.ALIGN_LEFT));
            neto.addCell(createCell("591,704", boldFont, Element.ALIGN_RIGHT));
            neto.addCell(createCell("ANTICIPOS", boldFont, Element.ALIGN_LEFT));
            neto.addCell(createCell("180,000", boldFont, Element.ALIGN_RIGHT));
            document.add(neto);

            // A pagar
            PdfPTable pagar = new PdfPTable(3);
            pagar.setWidthPercentage(100);
            pagar.setWidths(new float[] { 3, 0.5f, 2 });
            pagar.addCell(createCell("A PAGAR Ch.Nº", normalFont, Element.ALIGN_LEFT));
            pagar.addCell(createCell("$", normalFont, Element.ALIGN_RIGHT));
            pagar.addCell(createCell("411,704", normalFont, Element.ALIGN_RIGHT));
            document.add(pagar);

            // Son
            document.add(new Paragraph("Son : CUATROCIENTOS ONCE MIL SETECIENTOS CUATRO PESOS M/L", normalFont));
            document.add(new Paragraph("VIÑA DEL MAR, 30 DE ABRIL DE 2025", normalFont));
            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph(
                    "Certifico que he recibido copia de la presente Liquidación, y conforme el  saldo líquido indicado.",
                    normalFont));
            document.add(new Paragraph("Y no tengo cargo o cobro alguno posterior que hacer.", normalFont));
            document.add(new Paragraph(" ", normalFont));

            // Firmas
            PdfPTable firmas = new PdfPTable(2);
            firmas.setWidthPercentage(100);
            firmas.addCell(createCell("Vo. Bo. Administración", normalFont, Element.ALIGN_CENTER));
            firmas.addCell(createCell("Firma Trabajador", normalFont, Element.ALIGN_CENTER));
            document.add(firmas);

            document.close();
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ PDF ajustado generado correctamente.");

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error al generar PDF: " + e.getMessage());
        }
    }

    // Utilidad para crear celdas sin borde y alineadas
    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
