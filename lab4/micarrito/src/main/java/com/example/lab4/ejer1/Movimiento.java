package com.example.lab4.ejer1;

import java.time.LocalDateTime;

public class Movimiento {
    private final String tipo; 
    private final int cantidad;
    private final LocalDateTime fechaHora;

    public Movimiento(String tipo, int cantidad) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fechaHora = LocalDateTime.now();
    }

    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}   