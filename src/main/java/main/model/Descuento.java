package main.model;

import lombok.Data;

@Data
public class Descuento {
    private int id;
    private String rutEmpleado;
    private String mes; // formato "YYYY-MM"
    private String tipoDescuento; // ejemplo: "AFP", "Fonasa", "Antigüedad"
    private double monto;
}