package main.model;

import javafx.beans.property.*;

public class Inventario {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();

    public Inventario() {
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String value) {
        nombre.set(value);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(int value) {
        cantidad.set(value);
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }
}
