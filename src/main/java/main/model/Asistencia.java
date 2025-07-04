package main.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Asistencia {
    private int id; // clave primaria
    private String rutEmpleado;
    private LocalDate fecha;
    private LocalTime hora;
    private String tipo; // "entrada" o "salida"
}