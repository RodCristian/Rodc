package main.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import main.database.ConexionBD;
import main.model.Asistencia;

public class AsistenciaService {

    public static void registrarAsistencia(String rut, String tipo) {
        String sql = "INSERT INTO ASISTENCIA (rut_empleado, fecha, hora, tipo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);
            stmt.setString(2, LocalDate.now().toString()); // yyyy-MM-dd ✅
            stmt.setString(3, LocalTime.now().withNano(0).toString()); // HH:mm:ss ✅
            stmt.setString(4, tipo);

            int rows = stmt.executeUpdate();
            System.out.println("✅ Filas insertadas: " + rows + " para RUT: " + rut);

        } catch (Exception e) {
            System.out.println("❌ Error registrando asistencia: " + e.getMessage());
        }
    }

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
}