package com.example.lab4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Importamos explícitamente el del Ejercicio 1
import com.example.lab4.ejer1.Producto; 
import com.example.lab4.ejer2.CarritoCompra;
import com.example.lab4.ejer2.ServicioPrecio;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    // Instancia única Ejercicio 1
    private Producto prod1 = new Producto("P-001", "Laptop Gamer", 1500.0, 10);

    // Instancia única Ejercicio 2
    private ServicioPrecio servicioSimple = new ServicioPrecio() {
        @Override public double calcularDescuento(double t) { return t > 1000 ? t * 0.1 : 0; }
        @Override public double calcularImpuesto(double t) { return t * 0.18; }
    };
    private CarritoCompra carrito = new CarritoCompra(servicioSimple);

    @GetMapping
    public String verDashboard(Model model) {
        model.addAttribute("prod1", prod1);
        model.addAttribute("carrito", carrito);
        
        // Catálogo Ejercicio 2 (Usando ruta completa para evitar conflicto)
        model.addAttribute("catalogo", List.of(
            new com.example.lab4.ejer2.Producto("C1", "Mouse Pro", 45.0, true),
            new com.example.lab4.ejer2.Producto("C2", "Teclado Mecánico", 120.0, true),
            new com.example.lab4.ejer2.Producto("C3", "Monitor 4K", 400.0, false)
        ));
        
        return "dashboard"; // Debe existir src/main/resources/templates/dashboard.html
    }

    @PostMapping("/ejer1/add")
    public String ejer1Add(@RequestParam int cantidad) {
        try {
            prod1.agregarStock(cantidad);
        } catch (Exception e) { /* Log error */ }
        return "redirect:/dashboard";
    }

    @PostMapping("/ejer2/buy")
    public String ejer2Buy(@RequestParam String id, @RequestParam String nombre, @RequestParam double precio) {
        com.example.lab4.ejer2.Producto p = new com.example.lab4.ejer2.Producto(id, nombre, precio, true);
        carrito.agregarProducto(p, 1);
        return "redirect:/dashboard";
    }
}