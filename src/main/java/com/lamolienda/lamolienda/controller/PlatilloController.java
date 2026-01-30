package com.lamolienda.lamolienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lamolienda.lamolienda.model.Platillo;
import com.lamolienda.lamolienda.service.PlatilloService;

@Controller
@RequestMapping("/platillos")
public class PlatilloController {

    @Autowired
    private PlatilloService platilloService;

    // Mostrar todos los platillos
    @GetMapping
    public String listarPlatillos(Model model) {
        model.addAttribute("platillos", platilloService.listarPlatillos());
        model.addAttribute("contenido", "platillos");
        return "layout";
    }
    

    // Formulario para nuevo platillo
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("platillo", new Platillo());
        model.addAttribute("contenido", "form_platillo");
        return "layout";
    }

    // Guardar platillo nuevo o editado
    @PostMapping("/guardar")
    public String guardarPlatillo(@ModelAttribute Platillo platillo) {
        platilloService.guardarPlatillo(platillo);
        return "redirect:/platillos";
    }

    // Formulario de edición
    @GetMapping("/editar/{id}")
    public String editarPlatillo(@PathVariable Long id, Model model) {
        platilloService.obtenerPlatilloPorId(id).ifPresent(p -> model.addAttribute("platillo", p));
        model.addAttribute("contenido", "form_platillo");
        return "layout";
    }
    // Eliminar platillo
    @GetMapping("/eliminar/{id}")
    public String eliminarPlatillo(@PathVariable Long id) {
        platilloService.eliminarPlatillo(id);
        return "redirect:/platillos";
    }
}
