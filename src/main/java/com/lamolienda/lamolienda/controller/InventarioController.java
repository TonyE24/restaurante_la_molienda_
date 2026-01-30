package com.lamolienda.lamolienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lamolienda.lamolienda.model.Inventario;
import com.lamolienda.lamolienda.service.InventarioService;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("inventarios", inventarioService.listar());
        model.addAttribute("contenido", "inventario_listado");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("inventario", new Inventario());
        model.addAttribute("contenido", "inventario_formulario");
        return "layout";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Inventario inventario) {
        inventarioService.guardar(inventario);
        return "redirect:/inventario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Inventario inv = inventarioService.obtenerPorId(id).orElse(new Inventario());
        model.addAttribute("inventario", inv);
        model.addAttribute("contenido", "inventario_formulario");
        return "layout";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return "redirect:/inventario";
    }
}
