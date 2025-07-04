package main.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import main.database.ConexionBD;
import main.model.Asistencia;

public class AsistenciaService {

    public static void registrarAsistencia(String rutEmpleado) {
        String sql = "INSERT INTO ASISTENCIA (rut_empleado, fecha, hora, tipo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rutEmpleado);
            stmt.setString(2, LocalDate.now().toString());
            stmt.setString(3, LocalTime.now().toString());
            stmt.setString(4, "entrada");

            stmt.executeUpdate();
            System.out.println("✅ Asistencia registrada para: " + rutEmpleado);

        } catch (Exception e) {
            System.out.println("❌ Error registrando asistencia: " + e.getMessage());
        }
    }

    public static List<Asistencia> listarAsistenciasPorEmpleado(String rutEmpleado) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM ASISTENCIA WHERE rut_empleado = ? ORDER BY fecha DESC, hora DESC";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rutEmpleado);
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
            System.out.println("❌ Error al obtener asistencias: " + e.getMessage());
        }

        return lista;
    }
}