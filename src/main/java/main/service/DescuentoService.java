package main.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.database.ConexionBD;
import main.model.Descuento;

public class DescuentoService {

    public static void registrarDescuento(Descuento d) {
        String sql = "INSERT INTO DESCUENTO (rut_empleado, mes, tipo_descuento, monto) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getRutEmpleado());
            stmt.setString(2, d.getMes());
            stmt.setString(3, d.getTipoDescuento());
            stmt.setDouble(4, d.getMonto());

            stmt.executeUpdate();
            System.out.println("✅ Descuento registrado.");

        } catch (SQLException e) {
            System.out.println("❌ Error al registrar descuento: " + e.getMessage());
        }
    }

    public static List<Descuento> obtenerDescuentosPorMes(String rut, String mes) {
        List<Descuento> lista = new ArrayList<>();
        String sql = "SELECT * FROM DESCUENTO WHERE rut_empleado = ? AND mes = ?";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);
            stmt.setString(2, mes);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Descuento d = new Descuento();
                d.setId(rs.getInt("id"));
                d.setRutEmpleado(rs.getString("rut_empleado"));
                d.setMes(rs.getString("mes"));
                d.setTipoDescuento(rs.getString("tipo_descuento"));
                d.setMonto(rs.getDouble("monto"));
                lista.add(d);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener descuentos: " + e.getMessage());
        }

        return lista;
    }
}