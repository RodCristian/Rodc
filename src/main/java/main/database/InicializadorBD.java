package main.database;

import java.sql.Connection;
import java.sql.Statement;

public class InicializadorBD {
    public static void inicializar() {
        try (Connection conn = ConexionBD.conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS EMPLEADO (
                            rut TEXT PRIMARY KEY,
                            nombre TEXT NOT NULL,
                            apellido TEXT NOT NULL,
                            fecha_ingreso TEXT NOT NULL,
                            cargo TEXT,
                            sueldo_base REAL NOT NULL,
                            afp TEXT,
                            fonasa TEXT,
                            categoria TEXT,
                            estado TEXT
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS ASISTENCIA (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            rut_empleado TEXT NOT NULL,
                            fecha TEXT NOT NULL,
                            hora TEXT NOT NULL,
                            tipo TEXT DEFAULT 'entrada',
                            FOREIGN KEY (rut_empleado) REFERENCES EMPLEADO(rut)
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS LIQUIDACION (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            rut_empleado TEXT NOT NULL,
                            mes TEXT NOT NULL,
                            sueldo_base REAL,
                            total_horas REAL,
                            bonificaciones REAL,
                            descuentos REAL,
                            sueldo_liquido REAL,
                            fecha_generacion TEXT,
                            FOREIGN KEY (rut_empleado) REFERENCES EMPLEADO(rut)
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS DESCUENTO (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            rut_empleado TEXT NOT NULL,
                            mes TEXT NOT NULL,
                            tipo_descuento TEXT NOT NULL,
                            monto REAL NOT NULL,
                            FOREIGN KEY (rut_empleado) REFERENCES EMPLEADO(rut)
                        );
                    """);

            System.out.println("✅ Todas las tablas fueron creadas correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}
