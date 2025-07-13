package main.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import main.database.ConexionBD;
import main.model.Asistencia;

/**
 * Servicio para manejar las operaciones de asistencia de empleados.
 * Permite registrar asistencias, listar asistencias por empleado y calcular
 * horas trabajadas en un mes.
 */
public class AsistenciaService {
    /**
     * Registra una asistencia de un empleado.
     *
     * @param rut  RUT del empleado.
     * @param tipo Tipo de asistencia (entrada/salida).
     */
    public static void registrarAsistencia(String rut, String tipo) {
        /**
         * Registra una asistencia de un empleado en la base de datos.
         * Valida que el RUT no sea nulo o vacío.
         */

        // Validación del RUT si el RUT es nulo o vacío lanza un mensaje de error.
        if (rut == null || rut.isEmpty()) {
            System.out.println("❌ RUT no puede ser nulo o vacío.");
            return;
        }
        // Sentencia SQL para insertar una nueva asistencia.
        String sql = "INSERT INTO ASISTENCIA (rut_empleado, fecha, hora, tipo) VALUES (?, ?, ?, ?)";
        // Conexión a la base de datos y ejecución de la sentencia SQL.
        // Utiliza try-with-resources para asegurar el cierre de recursos.
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Establece los parámetros de la sentencia SQL.
            // Utiliza LocalDate y LocalTime para obtener la fecha y hora actuales.
            stmt.setString(1, rut);
            stmt.setString(2, LocalDate.now().toString()); // yyyy-MM-dd ✅
            stmt.setString(3, LocalTime.now().withNano(0).toString()); // HH:mm:ss ✅
            stmt.setString(4, tipo);
            // Ejecuta la sentencia SQL y obtiene el número de filas afectadas.
            // Si la inserción es exitosa, imprime el número de filas insertadas.
            int rows = stmt.executeUpdate();
            System.out.println("✅ Filas insertadas: " + rows + " para RUT: " + rut);
            // Manejo de excepciones para capturar errores durante la conexión o ejecución
            // de la sentencia SQL.
        } catch (Exception e) {
            System.out.println("❌ Error registrando asistencia: " + e.getMessage());
        }
    }

    /**
     * Lista las asistencias de un empleado o todas las asistencias si no se
     * especifica un RUT.
     *
     * @param rutEmpleado RUT del empleado (opcional).
     * @return Lista de asistencias.
     */
    public static List<Asistencia> listarAsistenciasPorEmpleado(String rutEmpleado) {
        List<Asistencia> lista = new ArrayList<>();
        String sql;
        if (rutEmpleado == null || rutEmpleado.isEmpty()) {
            sql = "SELECT * FROM ASISTENCIA ORDER BY fecha DESC, hora DESC";
        } else {
            sql = "SELECT * FROM ASISTENCIA WHERE rut_empleado = ? ORDER BY fecha DESC, hora DESC";
        }

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (rutEmpleado != null && !rutEmpleado.isEmpty()) {
                stmt.setString(1, rutEmpleado);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setRutEmpleado(rs.getString("rut_empleado"));
                a.setFecha(LocalDate.parse(rs.getString("fecha")));
                a.setHora(LocalTime.parse(rs.getString("hora")));
                a.setTipo(rs.getString("tipo"));
                lista.add(a);
            }

        } catch (Exception e) {
            System.out.println("❌ Error listando asistencias: " + e.getMessage());
        }
        return lista;
    }

    public static double calcularHorasMes(String rutEmpleado, String mes) {
        String sql = "SELECT fecha, hora, tipo FROM ASISTENCIA WHERE rut_empleado = ? AND substr(fecha,1,7) = ? ORDER BY fecha, hora";
        List<Asistencia> asistencias = new ArrayList<>();

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rutEmpleado);
            stmt.setString(2, mes);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setFecha(LocalDate.parse(rs.getString("fecha")));
                a.setHora(LocalTime.parse(rs.getString("hora")));
                a.setTipo(rs.getString("tipo"));
                asistencias.add(a);
            }
        } catch (Exception e) {
            System.out.println("❌ Error al calcular horas: " + e.getMessage());
            return 0;
        }

        double totalHoras = 0;
        LocalTime entrada = null;
        for (Asistencia a : asistencias) {
            if ("entrada".equalsIgnoreCase(a.getTipo())) {
                entrada = a.getHora();
            } else if ("salida".equalsIgnoreCase(a.getTipo()) && entrada != null) {
                totalHoras += java.time.Duration.between(entrada, a.getHora()).toMinutes() / 60.0;
                entrada = null;
            }
        }
        return totalHoras;
    }

    public static boolean tieneEntradaHoy(String rut, LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM ASISTENCIA WHERE rut_empleado = ? AND fecha = ? AND tipo = 'entrada'";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);
            stmt.setString(2, fecha.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("❌ Error verificando entrada: " + e.getMessage());
        }
        return false;
    }

    public static String obtenerUltimoTipoHoy(String rut, LocalDate fecha) {
        String sql = "SELECT tipo FROM ASISTENCIA WHERE rut_empleado = ? AND fecha = ? ORDER BY hora DESC LIMIT 1";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);
            stmt.setString(2, fecha.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tipo");
            }
        } catch (Exception e) {
            System.out.println("❌ Error obteniendo último tipo: " + e.getMessage());
        }
        return null; // No tiene registros hoy
    }

}