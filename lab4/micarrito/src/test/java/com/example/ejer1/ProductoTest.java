package com.example.ejer1;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.lab4.ejer1.Producto;

public class ProductoTest {
     private Producto producto;

    // Se ejecuta antes de CADA prueba para tener un objeto limpio
    @BeforeEach
    void setUp() {
        producto = new Producto("P-001", "Laptop", 1200.0, 10);
    }

    @Test
    @DisplayName("CP01: Creación correcta de un producto válido")
    void testCreacionExitosa() {
        assertAll(
            () -> assertEquals("P-001", producto.getCodigo()),
            () -> assertEquals(10, producto.consultarStock()),
            () -> assertEquals(12000.0, producto.obtenerValorTotal())
        );
    }

    // --- PRUEBAS DE EXCEPCIONES (ERRORES) ---

    @Test
    @DisplayName("CP02: Error al crear con código vacío")
    void testErrorCodigoVacio() {
        assertThrows(IllegalArgumentException.class, () ->  new Producto("  ", "Nombre", 10.0, 1));
    }

    @Test
    @DisplayName("CP03: Error al crear con código nulo")
    void testErrorCodigoNull() {
        assertThrows(IllegalArgumentException.class, () -> new Producto(null, "Nombre", 10.0, 1));
    }
    
    @Test
    @DisplayName("CP04: Error al crear con precio cero o negativo")
    void testErrorPrecioInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("P", "N", 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Producto("P", "N", -10.5, 1));
    }

    @Test
    @DisplayName("CP05: Error al crear con cantidad inicial negativa")
    void testErrorCantidadInicialNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("P", "N", 10.0, -5));
    }
}
