package com.example.lab4.ejer2;

public class Producto {
    private String id;
    private String nombre;
    private double precio;
    private boolean disponible;

    public Producto(String id, String nombre, double precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public boolean isDisponible() { return disponible; }
}