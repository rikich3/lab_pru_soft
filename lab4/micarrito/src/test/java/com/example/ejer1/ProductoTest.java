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
    @Test
    @DisplayName("CP06: Agregar stock correctamente y validar historial")
    void testAgregarStockExitoso() {
        producto.agregarStock(5);
        assertEquals(15, producto.consultarStock());
        assertEquals(1, producto.getMovimientos().size());
        assertEquals("ENTRADA", producto.getMovimientos().get(0).getTipo());
    }

    @Test
    @DisplayName("CP07: Error al agregar stock con cantidad negativa o cero")
    void testErrorAgregarStockInvalido() {
        assertThrows(IllegalArgumentException.class, () -> producto.agregarStock(-1));
        assertThrows(IllegalArgumentException.class, () -> producto.agregarStock(0));
    }

    @Test
    @DisplayName("CP08: Extraer stock correctamente")
    void testExtraerStockExitoso() {
        producto.extraerStock(5);
        assertEquals(5, producto.consultarStock());
        assertEquals("SALIDA", producto.getMovimientos().get(0).getTipo());
    }

    @Test
    @DisplayName("CP09: Error al extraer más stock del disponible")
    void testErrorExtraerStockInsuficiente() {
        assertThrows(IllegalStateException.class, () -> producto.extraerStock(11));
    }

    @Test
    @DisplayName("CP10: Error al extraer cantidad negativa")
    void testErrorExtraerStockNegativo() {
        assertThrows(IllegalArgumentException.class, () -> producto.extraerStock(-5));
    }

    @Test
    @DisplayName("CP11: Cálculo de valor total correcto")
    void testValorTotal() {
        assertEquals(12000.0, producto.obtenerValorTotal());
        producto.agregarStock(2);
        assertEquals(14400.0, producto.obtenerValorTotal());
    }
}
