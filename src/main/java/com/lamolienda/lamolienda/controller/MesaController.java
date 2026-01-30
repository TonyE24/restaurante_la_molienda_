package com.lamolienda.lamolienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lamolienda.lamolienda.model.Mesa;
import com.lamolienda.lamolienda.service.MesaService;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public String listarMesas(Model model) {
        model.addAttribute("mesas", mesaService.listarMesas());
        model.addAttribute("contenido", "mesas");
        return "layout";
    }
    
    // Formulario para nueva mesa
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaMesa(Model model) {
        model.addAttribute("mesa", new Mesa());
        model.addAttribute("contenido", "form_mesa");
        return "layout";
    }
    // Guardar nueva mesa
    @PostMapping("/guardar")
    public String guardarMesa(@ModelAttribute Mesa mesa) {
        mesaService.guardarMesa(mesa);
        return "redirect:/mesas";
    }

    // Editar mesa existente
    @GetMapping("/editar/{id}")
    public String editarMesa(@PathVariable Long id, Model model) {
        mesaService.obtenerMesaPorId(id).ifPresent(m -> model.addAttribute("mesa", m));
        model.addAttribute("contenido", "form_mesa");
        return "layout";
    }
    // Eliminar mesa
    @GetMapping("/eliminar/{id}")
    public String eliminarMesa(@PathVariable Long id) {
        mesaService.eliminarMesa(id);
        return "redirect:/mesas";
    }
}
