package main.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane; // Importa GridPane
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
//import javafx.application.Platform; // Para Platform.runLater si necesitas hacer algo en el hilo UI

public class MainController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private StackPane contenidoPane;
    @FXML
    private GridPane dashboardGridPane; // Inyecta el GridPane del dashboard

    // Una referencia para guardar el contenido inicial del dashboard
    private Parent initialDashboardContent;

    /**
     * Este método se llama automáticamente después de que el FXML ha sido cargado.
     * Es el lugar ideal para establecer la vista inicial (las tarjetas de
     * bienvenida)
     * y guardar una referencia a ellas.
     */
    @FXML
    public void initialize() {
        // Guarda una referencia al GridPane del dashboard (las tarjetas)
        // en el momento en que se inicializa el controlador.
        // Esto asume que el dashboardGridPane es el contenido inicial del StackPane
        // o que se cargará justo después.
        initialDashboardContent = dashboardGridPane;
        // Asegúrate de que el contenidoPane muestre el dashboard al inicio
        // (Esto ya debería suceder si el GridPane está directamente en el StackPane del
        // FXML)
        contenidoPane.getChildren().setAll(initialDashboardContent);
    }

    @FXML
    private void mostrarEmpleados(ActionEvent event) {
        cargarModulo("/empleado.fxml");
    }

    @FXML
    private void mostrarInventario(ActionEvent event) {
        cargarModulo("/inventario.fxml");
    }

    @FXML
    private void mostrarLiquidaciones(ActionEvent event) {
        cargarModulo("/liquidacion.fxml");
    }

    @FXML
    private void mostrarAsistencia(ActionEvent event) {
        cargarModulo("/asistencia.fxml");
    }

    /**
     * Método para volver a la pantalla principal (dashboard de tarjetas).
     * Este método será llamado por el botón "Inicio" de la barra superior.
     */
    @FXML
    private void mostrarDashboard(ActionEvent event) {
        // Vuelve a cargar el contenido inicial (las tarjetas) en el StackPane.
        if (initialDashboardContent != null) {
            contenidoPane.getChildren().setAll(initialDashboardContent);
        } else {
            // Esto no debería suceder si initialize() se ejecuta correctamente,
            // pero es una buena práctica tener un fallback o un log.
            System.err.println("Error: No se pudo cargar el contenido inicial del dashboard.");
            // O cargar el FXML del dashboard de nuevo si estuviera en un archivo separado
            // cargarModulo("/dashboard.fxml");
        }
    }

    /**
     * Método auxiliar para cargar cualquier archivo FXML en el 'contenidoPane'.
     * Maneja excepciones de forma más robusta.
     * 
     * @param fxmlPath La ruta al archivo FXML (ej. "/vistas/empleados.fxml")
     */
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