package com.example.lab4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

// Importamos los del Ejercicio 1 y 2
import com.example.lab4.ejer1.Producto; 
import com.example.lab4.ejer2.CarritoCompra;
import com.example.lab4.ejer2.ServicioPrecio;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    // Instancia única Ejercicio 1 (Inventario)
    private Producto prod1 = new Producto("P-001", "Laptop Gamer", 1500.0, 10);

    // Instancia única Ejercicio 2 (Carrito) con Mock de lógica simulada
    private ServicioPrecio servicioSimple = new ServicioPrecio() {
        @Override public double calcularDescuento(double t) { return t > 1000 ? t * 0.1 : 0; }
        @Override public double calcularImpuesto(double t) { return t * 0.18; }
    };
    private CarritoCompra carrito = new CarritoCompra(servicioSimple);

    @GetMapping
    public String verDashboard(Model model) {
        model.addAttribute("prod1", prod1);
        model.addAttribute("carrito", carrito);
        
        double subtotal = carrito.getItems().stream()
            .mapToDouble(i -> i.getProducto().getPrecio() * i.getCantidad())
            .sum();
        double descuento = servicioSimple.calcularDescuento(subtotal);
        double impuesto = servicioSimple.calcularImpuesto(subtotal);
        
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("descuento", descuento);
        model.addAttribute("impuesto", impuesto);

        model.addAttribute("catalogo", List.of(
            new com.example.lab4.ejer2.Producto("C1", "Mouse Pro", 45.0, true),
            new com.example.lab4.ejer2.Producto("C2", "Teclado Mecánico", 120.0, true),
            new com.example.lab4.ejer2.Producto("C3", "Monitor 4K", 400.0, false), // Desactivado
            new com.example.lab4.ejer2.Producto("C4", "Silla Ergonómica", 250.0, true),
            new com.example.lab4.ejer2.Producto("C5", "Auriculares Pro", 85.0, true)
        ));
        
        return "dashboard";
    }
    
    @PostMapping("/ejer1/add")
    public String ejer1Add(@RequestParam int cantidad, RedirectAttributes redirectAttributes) {
        try {
            prod1.agregarStock(cantidad);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEjer1", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/ejer1/remove")
    public String ejer1Remove(@RequestParam int cantidad, RedirectAttributes redirectAttributes) {
        try {
            prod1.extraerStock(cantidad);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEjer1", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/ejer2/buy")
    public String ejer2Buy(@RequestParam String id, @RequestParam String nombre, @RequestParam double precio, RedirectAttributes redirectAttributes) {
        try {
            com.example.lab4.ejer2.Producto p = new com.example.lab4.ejer2.Producto(id, nombre, precio, true);
            carrito.agregarProducto(p, 1);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEjer2", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/ejer2/remove")
    public String ejer2Remove(@RequestParam String id) {
        carrito.removerProducto(id);
        return "redirect:/dashboard";
    }

    @PostMapping("/ejer2/clear")
    public String ejer2Clear() {
        carrito.vaciar();
        return "redirect:/dashboard";
    }
}