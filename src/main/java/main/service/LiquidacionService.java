package main.service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.database.ConexionBD;
import main.model.Liquidacion;

public class LiquidacionService {

    public static void guardarLiquidacion(Liquidacion liq) {
        String sql = "INSERT INTO LIQUIDACION (rut_empleado, mes, sueldo_base, total_horas, bonificaciones, descuentos, sueldo_liquido, fecha_generacion) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, liq.getRutEmpleado());
            stmt.setString(2, liq.getMes());
            stmt.setDouble(3, liq.getSueldoBase());
            stmt.setDouble(4, liq.getTotalHoras());
            stmt.setDouble(5, liq.getBonificaciones());
            stmt.setDouble(6, liq.getDescuentos());
            stmt.setDouble(7, liq.getSueldoLiquido());
            stmt.setString(8, liq.getFechaGeneracion().toString());

            stmt.executeUpdate();
            System.out.println("✅ Liquidación guardada para " + liq.getRutEmpleado());

        } catch (SQLException e) {
            System.out.println("❌ Error al guardar liquidación: " + e.getMessage());
        }
    }

    public static List<Liquidacion> listarLiquidaciones(String rutEmpleado) {
        List<Liquidacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM LIQUIDACION WHERE rut_empleado = ? ORDER BY mes DESC";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rutEmpleado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Liquidacion l = new Liquidacion();
                l.setId(rs.getInt("id"));
                l.setRutEmpleado(rs.getString("rut_empleado"));
                l.setMes(rs.getString("mes"));
                l.setSueldoBase(rs.getDouble("sueldo_base"));
                l.setTotalHoras(rs.getDouble("total_horas"));
                l.setBonificaciones(rs.getDouble("bonificaciones"));
                l.setDescuentos(rs.getDouble("descuentos"));
                l.setSueldoLiquido(rs.getDouble("sueldo_liquido"));
                l.setFechaGeneracion(LocalDate.parse(rs.getString("fecha_generacion")));
                lista.add(l);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar liquidaciones: " + e.getMessage());
        }

        return lista;
    }

    public static void generarLiquidacion(String rut, String mes, double descuentos) {
        double horas = calcularHorasMes(rut, mes);
        double sueldoBase = obtenerSueldoBase(rut);
        double bonificaciones = calcularBonificaciones(rut, mes);
        double sueldoLiquido = sueldoBase + bonificaciones - descuentos;

        Liquidacion liq = new Liquidacion();
        liq.setRutEmpleado(rut);
        liq.setMes(mes);
        liq.setSueldoBase(sueldoBase);
        liq.setTotalHoras(horas);
        liq.setBonificaciones(bonificaciones);
        liq.setDescuentos(descuentos);
        liq.setSueldoLiquido(sueldoLiquido);
        liq.setFechaGeneracion(LocalDate.now());

        guardarLiquidacion(liq);
    }

    // Calcula las horas trabajadas en el mes usando AsistenciaService
    public static double calcularHorasMes(String rut, String mes) {
        return AsistenciaService.calcularHorasMes(rut, mes);
    }

    // Obtiene el sueldo base del empleado desde la base de datos
    public static double obtenerSueldoBase(String rut) {
        return EmpleadoService.obtenerSueldoBase(rut);
    }

    // Calcula bonificaciones (puedes personalizar la lógica)
    public static double calcularBonificaciones(String rut, String mes) {
        // Ejemplo: sin bonificaciones fijas
        return 0.0;
    }
}