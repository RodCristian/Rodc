package main.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Liquidacion {
    private int id;
    private String rutEmpleado;
    private String mes; // formato "YYYY-MM"
    private double sueldoBase;
    private double totalHoras;
    private double bonificaciones;
    private double descuentos;
    private double sueldoLiquido;
    private LocalDate fechaGeneracion;
}