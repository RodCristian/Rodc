package main.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.model.Inventario;
import main.service.InventarioService;

import java.time.LocalDate;

public class PlantaController {

    @FXML
    private ComboBox<Inventario> productoComboBox; // Para consumo

    @FXML
    private TextField cantidadField; // Para consumo

    @FXML
    private ComboBox<Inventario> productoProduccionComboBox; // Para producción

    @FXML
    private TextField cantidadProduccionField; // Para producción

    @FXML
    private DatePicker fechaPicker;

    @FXML
    private TableView<Inventario> consumoTableView;

    @FXML
    private TableColumn<Inventario, String> nombreColumn;

    @FXML
    private TableColumn<Inventario, Integer> cantidadColumn;

    private final InventarioService inventarioService = new InventarioService();

    private final ObservableList<Inventario> consumoObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Fecha actual
        fechaPicker.setValue(LocalDate.now());

        // Cargar productos para ambos ComboBox
        ObservableList<Inventario> productos = FXCollections
                .observableArrayList(inventarioService.obtenerTodoInventario());
        productoComboBox.setItems(productos);
        productoProduccionComboBox.setItems(productos);

        // Mostrar solo el nombre en los ComboBox
        productoComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Inventario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        productoComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Inventario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        productoProduccionComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Inventario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        productoProduccionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Inventario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        // Configurar columnas de tabla
        nombreColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        cantidadColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCantidad())
                        .asObject());

        consumoTableView.setItems(consumoObservable);
    }

    @FXML
    private void registrarConsumo() {
        Inventario producto = productoComboBox.getValue();
        String cantidadTexto = cantidadField.getText();
        LocalDate fecha = fechaPicker.getValue();

        if (producto == null || cantidadTexto.isBlank() || fecha == null) {
            mostrarAlerta("Campos requeridos", "Debe seleccionar producto, cantidad y fecha.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                mostrarAlerta("Error de formato", "La cantidad debe ser un número entero positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "La cantidad debe ser un número entero válido.");
            return;
        }

        // Registrar consumo
        inventarioService.registrarConsumoPlanta(producto.getId(), fecha.toString(), cantidad);

        // Mostrar en tabla
        Inventario consumo = new Inventario();
        consumo.setId(producto.getId());
        consumo.setNombre(producto.getNombre() + " (Consumo)");
        consumo.setCantidad(cantidad);
        consumoObservable.add(consumo);

        // Limpiar campos
        cantidadField.clear();
        productoComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void registrarProduccion() {
        Inventario producto = productoProduccionComboBox.getValue();
        String cantidadTexto = cantidadProduccionField.getText();
        LocalDate fecha = fechaPicker.getValue();

        if (producto == null || cantidadTexto.isBlank() || fecha == null) {
            mostrarAlerta("Campos requeridos", "Debe seleccionar producto, cantidad y fecha para la producción.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                mostrarAlerta("Error de formato", "La cantidad producida debe ser un número entero positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "La cantidad producida debe ser un número entero válido.");
            return;
        }

        // Registrar producción
        inventarioService.registrarProduccionPlanta(producto.getId(), fecha.toString(), cantidad);

        // Mostrar en tabla
        Inventario produccion = new Inventario();
        produccion.setId(producto.getId());
        produccion.setNombre(producto.getNombre() + " (Producción)");
        produccion.setCantidad(cantidad);
        consumoObservable.add(produccion);

        // Limpiar campos
        cantidadProduccionField.clear();
        productoProduccionComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void actualizarStock() {
        // Actualiza el stock restando consumos y sumando producciones
        LocalDate fecha = fechaPicker.getValue();
        if (fecha == null) {
            mostrarAlerta("Error", "Seleccione una fecha para actualizar stock.");
            return;
        }

        inventarioService.actualizarStockPorConsumoPlanta(fecha.toString());
        inventarioService.actualizarStockPorProduccionPlanta(fecha.toString());

        mostrarAlerta("Éxito", "Stock actualizado correctamente.");

        // Refrescar productos en ComboBoxes (para que muestren stock actualizado)
        ObservableList<Inventario> productosActualizados = FXCollections
                .observableArrayList(inventarioService.obtenerTodoInventario());
        productoComboBox.setItems(productosActualizados);
        productoProduccionComboBox.setItems(productosActualizados);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
