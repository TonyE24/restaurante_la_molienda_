package com.lamolienda.lamolienda.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lamolienda.lamolienda.model.DetallePedido;
import com.lamolienda.lamolienda.model.Factura;
import com.lamolienda.lamolienda.model.Pedido;
import com.lamolienda.lamolienda.repository.DetallePedidoRepository;
import com.lamolienda.lamolienda.repository.FacturaRepository;
import com.lamolienda.lamolienda.repository.PedidoRepository;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    public Factura guardarFactura(Factura factura) {
        Pedido pedido = factura.getPedido();
        Pedido pedidoFinal = pedidoRepository.findById(pedido.getId()).orElseThrow();
        factura.setPedido(pedidoFinal);

        // Obtener todos los detalles del pedido
        List<DetallePedido> detalles = detallePedidoRepository.findAll().stream()
                .filter(d -> d.getPedido().getId().equals(pedidoFinal.getId()))
                .toList();

        // Calcular subtotal
        double subtotal = detalles.stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                .sum();

        // Calcular IVA (13%)
        double iva = subtotal * 0.13;
        double total = subtotal + iva;

        factura.setIva(iva);
        factura.setTotal(total);

        return facturaRepository.save(factura);
    }

    public void eliminarFactura(Long id) {
        facturaRepository.deleteById(id);
    }

    public Factura obtenerFacturaPorId(Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    public boolean existeFacturaParaPedido(Long pedidoId) {
        return facturaRepository.findAll().stream()
                .anyMatch(f -> f.getPedido().getId().equals(pedidoId));
    }

    // ✅ Nuevo: obtener los detalles de la factura
    public List<DetallePedido> obtenerDetallesPorFactura(Factura factura) {
        Long pedidoId = factura.getPedido().getId();
        return detallePedidoRepository.findAll().stream()
                .filter(d -> d.getPedido().getId().equals(pedidoId))
                .toList();
    }

    public Map<LocalDate, Double> resumenPorDia() {
    return facturaRepository.findAll().stream()
        .collect(Collectors.groupingBy(
            factura -> factura.getFecha().toLocalDate(),
            Collectors.summingDouble(Factura::getTotal)
        ));
}
public Map<YearMonth, Double> resumenPorMes() {
    return facturaRepository.findAll().stream()
        .collect(Collectors.groupingBy(
            factura -> YearMonth.from(factura.getFecha()),
            Collectors.summingDouble(Factura::getTotal)
        ));
}

public Map<String, Integer> obtenerTop5PlatillosMasVendidos() {
    List<DetallePedido> detalles = detallePedidoRepository.findAll();

    Map<String, Integer> conteo = new HashMap<>();

    for (DetallePedido d : detalles) {
        String nombre = d.getPlatillo().getNombre();
        int cantidad = d.getCantidad();
        conteo.put(nombre, conteo.getOrDefault(nombre, 0) + cantidad);
    }

    // Ordenar por mayor cantidad y tomar los primeros 5
    return conteo.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(5)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
}

}
