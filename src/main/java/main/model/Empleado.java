package main.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Empleado {
    private String rut;
    private String nombre;
    private String apellido;
    private LocalDate fechaIngreso;
    private String cargo;
    private double sueldoBase;
    private String afp;
    private String fonasa;
    private String categoria;
    private String estado;
}
