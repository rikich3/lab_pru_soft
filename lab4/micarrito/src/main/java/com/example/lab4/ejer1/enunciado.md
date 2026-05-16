Descripción General
---

Desarrolle un programa para la gestión de inventario que permita controlar el stock de productos. El programa
Debe mantener información de productos y registrar movimientos (entradas y salidas). Este ejercicio enfatiza
validaciones y casos límite.

DIVISION DE RESPONSABILIDADES 

1. Requisitos Funcionales

Clase Producto con atributos: código, nombre, precio, cantidad
Validar que el código no sea vacío
Validar que la cantidad nunca sea negativa
Validar que el precio sea positivo
Método agregarStock(cantidad): incrementa el stock
Método extraerStock(cantidad): decrementa el stock
Método consultarStock(): retorna cantidad disponible
Método obtenerValorTotal(): retorna precio * cantidad
Lanzar excepciones para operaciones inválidas
Registrar fecha y hora de cada movimiento

2. Requisitos de Pruebas
Mínimo 12 casos de prueba
Cobertura de código al 100%
Cobertura de casos excepcionales (valores negativos, nulos)
Al menos una prueba parametrizada
Usar @BeforeEach para inicialización
Usar @DisplayName para descripciones legibles
Validar mensajes de error en excepciones

3. Sugerencias de Estructura de Clases
Clase Producto:
Atributos: código (String), nombre (String), precio (double), cantidad (int)
Métodos: getters, agregarStock(), extraerStock(), consultarStock()
