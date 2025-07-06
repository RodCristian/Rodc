package main.service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.database.ConexionBD;
import main.model.Empleado;

public class EmpleadoService {

    // Agregar empleado
    public static void agregarEmpleado(Empleado emp) {
        String sql = "INSERT INTO EMPLEADO (rut, nombre, apellido, fecha_ingreso, cargo, sueldo_base, afp, fonasa, categoria, estado) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emp.getRut());
            stmt.setString(2, emp.getNombre());
            stmt.setString(3, emp.getApellido());
            stmt.setString(4, emp.getFechaIngreso().toString());
            stmt.setString(5, emp.getCargo());
            stmt.setDouble(6, emp.getSueldoBase());
            stmt.setString(7, emp.getAfp());
            stmt.setString(8, emp.getFonasa());
            stmt.setString(9, emp.getCategoria());
            stmt.setString(10, emp.getEstado());

            stmt.executeUpdate();
            System.out.println("✅ Empleado agregado correctamente.");

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar empleado: " + e.getMessage());
        }
    }

    // Listar empleados
    public static List<Empleado> listarEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM EMPLEADO";

        try (Connection conn = ConexionBD.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Empleado emp = new Empleado();
                emp.setRut(rs.getString("rut"));
                emp.setNombre(rs.getString("nombre"));
                emp.setApellido(rs.getString("apellido"));
                emp.setFechaIngreso(LocalDate.parse(rs.getString("fecha_ingreso")));
                emp.setCargo(rs.getString("cargo"));
                emp.setSueldoBase(rs.getDouble("sueldo_base"));
                emp.setAfp(rs.getString("afp"));
                emp.setFonasa(rs.getString("fonasa"));
                emp.setCategoria(rs.getString("categoria"));
                emp.setEstado(rs.getString("estado"));

                lista.add(emp);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar empleados: " + e.getMessage());
        }

        return lista;
    }

    // Buscar por RUT
    public static Empleado buscarEmpleado(String rut) {
        String sql = "SELECT * FROM EMPLEADO WHERE rut = ?";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Empleado emp = new Empleado();
                emp.setRut(rs.getString("rut"));
                emp.setNombre(rs.getString("nombre"));
                emp.setApellido(rs.getString("apellido"));
                emp.setFechaIngreso(LocalDate.parse(rs.getString("fecha_ingreso")));
                emp.setCargo(rs.getString("cargo"));
                emp.setSueldoBase(rs.getDouble("sueldo_base"));
                emp.setAfp(rs.getString("afp"));
                emp.setFonasa(rs.getString("fonasa"));
                emp.setCategoria(rs.getString("categoria"));
                emp.setEstado(rs.getString("estado"));
                return emp;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al buscar empleado: " + e.getMessage());
        }

        return null;
    }

    // Eliminar por RUT
    public static void eliminarEmpleado(String rut) {
        String sql = "DELETE FROM EMPLEADO WHERE rut = ?";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("🗑️ Empleado eliminado: " + rut);
            } else {
                System.out.println("⚠️ No se encontró el empleado.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar empleado: " + e.getMessage());
        }
    }

    public static List<Empleado> obtenerEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM EMPLEADO";
        try (Connection conn = ConexionBD.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Empleado emp = new Empleado();
                emp.setRut(rs.getString("rut"));
                emp.setNombre(rs.getString("nombre"));
                emp.setApellido(rs.getString("apellido"));
                emp.setFechaIngreso(LocalDate.parse(rs.getString("fecha_ingreso")));
                emp.setCargo(rs.getString("cargo"));
                emp.setSueldoBase(rs.getDouble("sueldo_base"));
                emp.setAfp(rs.getString("afp"));
                emp.setFonasa(rs.getString("fonasa"));
                emp.setCategoria(rs.getString("categoria"));
                emp.setEstado(rs.getString("estado"));
                lista.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static double obtenerSueldoBase(String rut) {
        String sql = "SELECT sueldo_base FROM EMPLEADO WHERE rut = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rut);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("sueldo_base");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al obtener sueldo base: " + e.getMessage());
        }
        return 0.0;
    }
}
