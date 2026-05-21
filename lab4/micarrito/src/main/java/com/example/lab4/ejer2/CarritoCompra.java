package com.example.lab4.ejer2;

import java.util.*;

public class CarritoCompra {
    private List<ItemCarrito> items = new ArrayList<>();
    private List<String> historial = new ArrayList<>();
    private ServicioPrecio servicioPrecio;

    public CarritoCompra(ServicioPrecio servicioPrecio) {
        this.servicioPrecio = servicioPrecio;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        if (!producto.disponible()) throw new IllegalStateException("Producto no disponible");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");

        items.stream()
            .filter(i -> i.getProducto().id().equals(producto.id()))
            .findFirst()
            .ifPresentOrElse(
                i -> {
                    i.setCantidad(i.getCantidad() + cantidad);
                    historial.add("Actualizada cantidad: " + producto.nombre());
                },
                () -> {
                    items.add(new ItemCarrito(producto, cantidad));
                    historial.add("Agregado: " + producto.nombre());
                }
            );
    }

    public void removerProducto(String id) {
        items.removeIf(i -> i.getProducto().id().equals(id));
        historial.add("Removido producto ID: " + id);
    }

    public void vaciar() {
        items.clear();
        historial.add("Carrito vaciado");
    }

    public double calcularTotal() {
        double subtotal = items.stream()
                .mapToDouble(i -> i.getProducto().precio() * i.getCantidad())
                .sum();
        if (subtotal == 0) return 0;
        return subtotal - servicioPrecio.calcularDescuento(subtotal) + servicioPrecio.calcularImpuesto(subtotal);
    }

    public String obtenerResumenCompra() {
        return "Productos: " + items.size() + " | Total: $" + calcularTotal();
    }

    public List<ItemCarrito> getItems() { return items; }
    public List<String> getHistorial() { return historial; }
}