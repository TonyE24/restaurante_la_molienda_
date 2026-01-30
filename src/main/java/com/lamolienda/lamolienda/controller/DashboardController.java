package com.lamolienda.lamolienda.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.lamolienda.lamolienda.model.Mesa;
import com.lamolienda.lamolienda.model.Platillo;
import com.lamolienda.lamolienda.service.FacturaService;
import com.lamolienda.lamolienda.service.MesaService;
import com.lamolienda.lamolienda.service.PlatilloService;

@Controller
public class DashboardController {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private PlatilloService platilloService;

    @Autowired
    private FacturaService facturaService;

    // Ruta principal con tarjetas
    @GetMapping("/")
    public String verDashboard(Model model) {
        long platillosDisponibles = platilloService.listarPlatillos().stream()
                .filter(Platillo::getDisponible).count();

        long mesasLibres = mesaService.listarMesas().stream()
                .filter(m -> m.getEstado() == Mesa.Estado.LIBRE).count();

        long mesasOcupadas = mesaService.listarMesas().stream()
                .filter(m -> m.getEstado() == Mesa.Estado.OCUPADA).count();

        model.addAttribute("platillosDisponibles", platillosDisponibles);
        model.addAttribute("mesasLibres", mesasLibres);
        model.addAttribute("mesasOcupadas", mesasOcupadas);
        model.addAttribute("contenido", "dashboard");

        return "layout";
    }

    @GetMapping("/dashboard")
    public String verGraficasDashboard(Model model) {
        // Ingresos por día
        Map<LocalDate, Double> resumenPorDia = facturaService.resumenPorDia();
        List<String> fechas = resumenPorDia.keySet().stream().map(LocalDate::toString).toList();
        List<Double> totales = resumenPorDia.values().stream().toList();
    
        // Top platillos
        Map<String, Integer> topPlatillos = facturaService.obtenerTop5PlatillosMasVendidos();
        List<String> nombresPlatillos = new ArrayList<>(topPlatillos.keySet());
        List<Integer> cantidadesVendidas = new ArrayList<>(topPlatillos.values());
    
        model.addAttribute("fechas", fechas);
        model.addAttribute("totales", totales);
        model.addAttribute("nombresPlatillos", nombresPlatillos);
        model.addAttribute("cantidadesVendidas", cantidadesVendidas);
        model.addAttribute("contenido", "dashboard_graficas");
    
        return "layout";
    }
}    