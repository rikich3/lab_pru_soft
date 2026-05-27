package com.example.ejer2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.lab4.ejer2.CarritoCompra;
import com.example.lab4.ejer2.Producto;
import com.example.lab4.ejer2.ServicioPrecio;

@ExtendWith(MockitoExtension.class)
public class CarritoCompraTest {

    @Mock
    private ServicioPrecio servicioPrecioMock;
    
    private CarritoCompra carrito;
    private Producto prodValido;
    private Producto prodNoDisponible;

    @BeforeEach
    void setUp() {
        // Inicializamos el carrito inyectando el Mock de Mockito
        carrito = new CarritoCompra(servicioPrecioMock);
        prodValido = new Producto("P1", "Laptop", 1000.0, true);
        prodNoDisponible = new Producto("P2", "Mouse", 50.0, false);
    }

    @Nested
    @DisplayName("1. Operaciones Básicas del Carrito (Sin Mocks Complejos)")
    class OperacionesBasicas {

        @Test
        @DisplayName("CP01: Agregar producto disponible exitosamente")
        void testAgregarProducto() {
            carrito.agregarProducto(prodValido, 1);
            assertEquals(1, carrito.getItems().size());
        }

        @Test
        @DisplayName("CP02: Validar producto duplicado (actualiza cantidad)")
        void testProductoDuplicado() {
            carrito.agregarProducto(prodValido, 1);
            carrito.agregarProducto(prodValido, 2); // Agrega 2 más
            assertEquals(1, carrito.getItems().size(), "No debe duplicar el item");
            assertEquals(3, carrito.getItems().get(0).getCantidad()); //prodValido es el item 0, cantidad total debe ser 3 (1 + 2)
        }

        @Test
        @DisplayName("CP03: Validar que no se agreguen productos indisponibles")
        void testProductoIndisponible() {
            assertThrows(IllegalStateException.class, () -> carrito.agregarProducto(prodNoDisponible, 1));
        }

        @Test
        @DisplayName("CP04: Validar que no se agregue cantidad negativa o cero")
        void testCantidadInvalida() {
            assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto(prodValido, 0));
            assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto(prodValido, -5));
        }

        @Test
        @DisplayName("CP05: Remover producto del carrito")
        void testRemoverProducto() {
            carrito.agregarProducto(prodValido, 1);
            carrito.removerProducto("P1");
            assertTrue(carrito.getItems().isEmpty());
        }

        @Test
        @DisplayName("CP06: Vaciar carrito completamente")
        void testVaciarCarrito() {
            carrito.agregarProducto(prodValido, 1);
            carrito.vaciar();
            assertTrue(carrito.getItems().isEmpty());
            assertTrue(carrito.getHistorial().contains("Carrito vaciado"));
        }

        @Test
        @DisplayName("CP07: Verificar historial de operaciones")
        void testHistorial() {
            carrito.agregarProducto(prodValido, 1);
            carrito.removerProducto("P1");
            assertEquals(2, carrito.getHistorial().size());
        }
    }

    @Nested
    @DisplayName("2. Cálculos y Uso de Mockito (Servicio simulado)")
    class CalculosConMockito {

        @Test
        @DisplayName("CP08: Validar carrito vacío (total = 0)")
        void testCarritoVacioTotalCero() {
            assertEquals(0.0, carrito.calcularTotal());
            // Verificamos que si está vacío, NO llama al servicio externo
            verifyNoInteractions(servicioPrecioMock);
        }

        @Test
        @DisplayName("CP09: Validar cálculo correcto con descuento e impuesto")
        void testCalculoTotalConMock() {
            carrito.agregarProducto(prodValido, 2); // Subtotal: 2000.0

            // Simulamos respuestas del servicio externo usando Mockito
            when(servicioPrecioMock.calcularDescuento(2000.0)).thenReturn(200.0);
            when(servicioPrecioMock.calcularImpuesto(2000.0)).thenReturn(360.0);

            // 2000 - 200 + 360 = 2160.0
            assertEquals(2160.0, carrito.calcularTotal());
        }

        @Test
        @DisplayName("CP10: Verificar llamadas exactas a ServicioPrecio (verify)")
        void testVerificarLlamadasAlMock() {
            carrito.agregarProducto(prodValido, 1); // Subtotal 1000
            carrito.calcularTotal();

            // Verificamos que Mockito registró que los métodos fueron llamados
            verify(servicioPrecioMock, times(1)).calcularDescuento(1000.0);
            verify(servicioPrecioMock, times(1)).calcularImpuesto(1000.0);
        }
        
        @Test
        @DisplayName("CP11: Obtener resumen de compra correcto")
        void testResumenCompra() {
            carrito.agregarProducto(prodValido, 1);
            when(servicioPrecioMock.calcularDescuento(anyDouble())).thenReturn(0.0);
            when(servicioPrecioMock.calcularImpuesto(anyDouble())).thenReturn(0.0);
            
            String resumen = carrito.obtenerResumenCompra();
            assertTrue(resumen.contains("Productos: 1"));
            assertTrue(resumen.contains("Total: $1000.0"));
        }
    }

    @Nested
    @DisplayName("3. Pruebas Parametrizadas y Casos Límite")
    class CasosLimiteYParametrizadas {

        @ParameterizedTest
        @CsvSource({
            "100.0, 10.0, 18.0, 108.0",  // Subtotal 100, desc 10, imp 18 -> Total 108
            "50.0,  0.0,   5.0,  55.0",  // Subtotal 50,  desc 0,  imp 5  -> Total 55
            "200.0, 50.0,  0.0,  150.0"  // Subtotal 200, desc 50, imp 0  -> Total 150
        })
        @DisplayName("CP12 a CP14: Pruebas parametrizadas para diferentes montos")
        void testCalculosParametrizados(double precioBase, double descuentoMock, double impuestoMock, double totalEsperado) {
            Producto pTemp = new Producto("P-Temp", "Temp", precioBase, true);
            carrito.agregarProducto(pTemp, 1);

            when(servicioPrecioMock.calcularDescuento(precioBase)).thenReturn(descuentoMock);
            when(servicioPrecioMock.calcularImpuesto(precioBase)).thenReturn(impuestoMock);

            assertEquals(totalEsperado, carrito.calcularTotal());
        }

        @Test
        @DisplayName("CP15: Caso límite - carrito con 1 producto")
        void testLimiteUnProducto() {
            carrito.agregarProducto(prodValido, 1);
            assertEquals(1, carrito.getItems().size());
            assertEquals(1, carrito.getItems().get(0).getCantidad());
        }

        @Test
        @DisplayName("CP16: Caso límite - carrito con 100 productos")
        void testLimiteCienProductos() {
            carrito.agregarProducto(prodValido, 100);
            assertEquals(1, carrito.getItems().size());
            assertEquals(100, carrito.getItems().get(0).getCantidad());
        }
        
        @Test
        @DisplayName("CP17: Validar atributos del Producto")
        void testGettersProducto() {
            assertEquals("P1", prodValido.getId());
            assertEquals("Laptop", prodValido.getNombre());
            assertEquals(1000.0, prodValido.getPrecio());
            assertTrue(prodValido.isDisponible());
        }

        @Test
        @DisplayName("CP18: Validar encapsulamiento del ItemCarrito")
        void testItemCarrito() {
            carrito.agregarProducto(prodValido, 1);
            var item = carrito.getItems().get(0);
            assertNotNull(item.getProducto());
            item.setCantidad(5);
            assertEquals(5, item.getCantidad());
        }
    }
}