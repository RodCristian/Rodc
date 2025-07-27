package main.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.database.ConexionBD;
import main.model.Inventario;

public class OficinaController {

    @FXML
    private ComboBox<String> productoComboBox;

    @FXML
    private TableView<Inventario> tableView;

    @FXML
    private TableColumn<Inventario, String> nombreColumn;

    @FXML
    private TableColumn<Inventario, Integer> cantidadColumn;

    private final ObservableList<Inventario> inventarioObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas tabla
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Configurar tabla
        tableView.setItems(inventarioObservable);

        // Cargar nombres de productos en ComboBox
        cargarNombresProductos();

        // Mostrar todo el inventario inicialmente
        mostrarTodo();
    }

    private void cargarNombresProductos() {
        List<Inventario> productos = obtenerStockReal();
        ObservableList<String> nombres = FXCollections.observableArrayList();
        for (Inventario p : productos) {
            nombres.add(p.getNombre());
        }
        productoComboBox.setItems(nombres);
    }

    @FXML
    private void buscarStock() {
        String nombreSeleccionado = productoComboBox.getValue();

        if (nombreSeleccionado == null || nombreSeleccionado.isEmpty()) {
            mostrarAlerta("Filtro vacío", "Seleccione un producto para filtrar.");
            return;
        }

        inventarioObservable.setAll(obtenerStockRealPorNombre(nombreSeleccionado));
    }

    @FXML
    private void mostrarTodo() {
        inventarioObservable.setAll(obtenerStockReal());
        productoComboBox.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public List<Inventario> obtenerStockReal() {
        List<Inventario> lista = new ArrayList<>();
        String sql = """
                SELECT p.id, p.nombre,
                       p.cantidad - IFNULL(SUM(cp.cantidad_usada), 0) AS stock_real
                FROM PRODUCTO p
                LEFT JOIN CONSUMO_PLANTA cp ON p.id = cp.producto_id
                GROUP BY p.id, p.nombre, p.cantidad
                """;

        try (Connection conn = ConexionBD.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Inventario i = new Inventario();
                i.setId(rs.getInt("id"));
                i.setNombre(rs.getString("nombre"));
                i.setCantidad(rs.getInt("stock_real"));
                lista.add(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Inventario> obtenerStockRealPorNombre(String nombreFiltro) {
        List<Inventario> lista = new ArrayList<>();
        String sql = """
                SELECT p.id, p.nombre,
                       p.cantidad - IFNULL(SUM(cp.cantidad_usada), 0) AS stock_real
                FROM PRODUCTO p
                LEFT JOIN CONSUMO_PLANTA cp ON p.id = cp.producto_id
                WHERE p.nombre = ?
                GROUP BY p.id, p.nombre, p.cantidad
                """;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreFiltro);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Inventario i = new Inventario();
                i.setId(rs.getInt("id"));
                i.setNombre(rs.getString("nombre"));
                i.setCantidad(rs.getInt("stock_real"));
                lista.add(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
