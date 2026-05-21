package com.example.lab4.ejer1.controller;

import com.example.lab4.ejer1.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario")
public class InventarioController {
    private Producto producto = new Producto("P-001", "Laptop Gamer", 1500.0, 10);

    @GetMapping
    public String verInventario(Model model) {
        model.addAttribute("producto", producto);
        return "inventario";
    }

    @PostMapping("/agregar")
    public String agregarStock(@RequestParam int cantidad) {
        try {
            producto.agregarStock(cantidad);
        } catch (Exception e) {
        }
        return "redirect:/inventario";
    }

    @ResponseBody
    @GetMapping("/api")
    public Producto obtenerProductoApi() {
        return producto;
    }
}