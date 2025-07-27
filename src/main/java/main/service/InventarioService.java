package main.service;

import main.database.ConexionBD;
import main.model.Inventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioService {

    // Agregar producto
    public void agregarProducto(String nombre, int cantidad) {
        String sql = "INSERT INTO PRODUCTO (nombre, cantidad) VALUES (?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, cantidad);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener todos los productos
    public List<Inventario> obtenerTodoInventario() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTO";

        try (Connection conn = ConexionBD.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Inventario i = new Inventario();
                i.setId(rs.getInt("id"));
                i.setNombre(rs.getString("nombre"));
                i.setCantidad(rs.getInt("cantidad"));
                lista.add(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Registrar consumo en CONSUMO_PLANTA
    public void registrarConsumoPlanta(int productoId, String fecha, int cantidadUsada) {
        String sql = "INSERT INTO CONSUMO_PLANTA (producto_id, fecha, cantidad_usada) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productoId);
            pstmt.setString(2, fecha);
            pstmt.setInt(3, cantidadUsada);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registrarProduccionPlanta(int productoId, String fecha, int cantidadProducida) {
        String sqlInsert = "INSERT INTO PRODUCCION_PLANTA (producto_id, fecha, cantidad_producida) VALUES (?, ?, ?)";
        String sqlUpdateStock = "UPDATE PRODUCTO SET cantidad = cantidad + ? WHERE id = ?";

        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false); // Inicio de transacción

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
                    PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateStock)) {

                // Insertar registro de producción
                pstmtInsert.setInt(1, productoId);
                pstmtInsert.setString(2, fecha);
                pstmtInsert.setInt(3, cantidadProducida);
                pstmtInsert.executeUpdate();

                // Actualizar stock en PRODUCTO
                pstmtUpdate.setInt(1, cantidadProducida);
                pstmtUpdate.setInt(2, productoId);
                pstmtUpdate.executeUpdate();

                conn.commit(); // Confirmar transacción
            } catch (SQLException e) {
                conn.rollback(); // Revertir si hay error
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar stock restando consumo
    public void actualizarStockPorConsumoPlanta(String fecha) {
        String sqlSelectConsumo = "SELECT producto_id, SUM(cantidad_usada) AS total_consumo FROM CONSUMO_PLANTA WHERE fecha = ? GROUP BY producto_id";
        String sqlUpdateStock = "UPDATE PRODUCTO SET cantidad = cantidad - ? WHERE id = ?";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement psConsumo = conn.prepareStatement(sqlSelectConsumo);
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateStock)) {

            psConsumo.setString(1, fecha);
            ResultSet rs = psConsumo.executeQuery();

            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                int totalConsumo = rs.getInt("total_consumo");

                psUpdate.setInt(1, totalConsumo);
                psUpdate.setInt(2, productoId);
                psUpdate.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar stock sumando producción
    public void actualizarStockPorProduccionPlanta(String fecha) {
        String sqlSelectProduccion = "SELECT producto_id, SUM(cantidad_producida) AS total_produccion FROM PRODUCCION_PLANTA WHERE fecha = ? GROUP BY producto_id";
        String sqlUpdateStock = "UPDATE PRODUCTO SET cantidad = cantidad + ? WHERE id = ?";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement psProduccion = conn.prepareStatement(sqlSelectProduccion);
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateStock)) {

            psProduccion.setString(1, fecha);
            ResultSet rs = psProduccion.executeQuery();

            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                int totalProduccion = rs.getInt("total_produccion");

                psUpdate.setInt(1, totalProduccion);
                psUpdate.setInt(2, productoId);
                psUpdate.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Trae todo el stock real (sumando producción - consumo)
    public List<Inventario> obtenerStockReal() {
        List<Inventario> lista = new ArrayList<>();
        String sql = """
                SELECT p.id, p.nombre,
                    (p.cantidad + IFNULL(prod.total_producido,0) - IFNULL(consumo.total_consumo,0)) AS cantidad_real
                FROM PRODUCTO p
                LEFT JOIN (
                    SELECT producto_id, SUM(cantidad_producida) AS total_producido
                    FROM PRODUCCION_PLANTA
                    GROUP BY producto_id
                ) prod ON p.id = prod.producto_id
                LEFT JOIN (
                    SELECT producto_id, SUM(cantidad_usada) AS total_consumo
                    FROM CONSUMO_PLANTA
                    GROUP BY producto_id
                ) consumo ON p.id = consumo.producto_id
                """;

        try (Connection conn = ConexionBD.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Inventario i = new Inventario();
                i.setId(rs.getInt("id"));
                i.setNombre(rs.getString("nombre"));
                i.setCantidad(rs.getInt("cantidad_real"));
                lista.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Trae stock real filtrando por nombre (case insensitive, contiene)
    public List<Inventario> obtenerStockRealPorNombre(String nombreFiltro) {
        List<Inventario> lista = new ArrayList<>();
        String sql = """
                SELECT p.id, p.nombre,
                    (p.cantidad + IFNULL(prod.total_producido,0) - IFNULL(consumo.total_consumo,0)) AS cantidad_real
                FROM PRODUCTO p
                LEFT JOIN (
                    SELECT producto_id, SUM(cantidad_producida) AS total_producido
                    FROM PRODUCCION_PLANTA
                    GROUP BY producto_id
                ) prod ON p.id = prod.producto_id
                LEFT JOIN (
                    SELECT producto_id, SUM(cantidad_usada) AS total_consumo
                    FROM CONSUMO_PLANTA
                    GROUP BY producto_id
                ) consumo ON p.id = consumo.producto_id
                WHERE LOWER(p.nombre) LIKE ?
                """;
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombreFiltro.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Inventario i = new Inventario();
                i.setId(rs.getInt("id"));
                i.setNombre(rs.getString("nombre"));
                i.setCantidad(rs.getInt("cantidad_real"));
                lista.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
