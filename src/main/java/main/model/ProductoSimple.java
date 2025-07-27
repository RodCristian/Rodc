package main.model;

import javafx.beans.property.*;

public class ProductoSimple {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();

    public ProductoSimple(int id, String nombre, int cantidad) {
        this.id.set(id);
        this.nombre.set(nombre);
        this.cantidad.set(cantidad);
    }

    public ProductoSimple(int id, String nombre) {
        this(id, nombre, 0);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }
}
