package com.example.lab4.ejer1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private int cantidad;
    private List<Movimiento> movimientos;

    public Producto(String codigo, String nombre, double precio, int cantidad) {

        if (codigo == null || codigo.trim().isEmpty()) 
            throw new IllegalArgumentException("El código no puede estar vacío");
        if (precio <= 0) 
            throw new IllegalArgumentException("El precio debe ser positivo");
        if (cantidad < 0) 
            throw new IllegalArgumentException("La cantidad nunca puede ser negativa");

        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.movimientos = new ArrayList<>();
    }

    public void agregarStock(int cantidadSumar) {
        if (cantidadSumar <= 0) 
            throw new IllegalArgumentException("La cantidad a agregar debe ser positiva");
        
        this.cantidad += cantidadSumar;
        this.movimientos.add(new Movimiento("ENTRADA", cantidadSumar));
    }

    public void extraerStock(int cantidadRestar) {
        if (cantidadRestar <= 0) 
            throw new IllegalArgumentException("La cantidad a extraer debe ser positiva");
        if (cantidadRestar > this.cantidad) 
            throw new IllegalStateException("Stock insuficiente");

        this.cantidad -= cantidadRestar;
        this.movimientos.add(new Movimiento("SALIDA", cantidadRestar));
    }

    public int consultarStock() {  
        return this.cantidad; 
    }

    public double obtenerValorTotal() {
         return this.precio * this.cantidad; 
        }

    public List<Movimiento> getMovimientos() { 
        return Collections.unmodifiableList(movimientos); 
    }
    
    public String getCodigo() { 
        return codigo; 
    }
    public String getNombre() { 
        return nombre; 
    }
}