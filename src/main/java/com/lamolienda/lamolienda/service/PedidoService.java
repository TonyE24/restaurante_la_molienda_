package com.lamolienda.lamolienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lamolienda.lamolienda.model.DetallePedido;
import com.lamolienda.lamolienda.model.Pedido;
import com.lamolienda.lamolienda.repository.DetallePedidoRepository;
import com.lamolienda.lamolienda.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    // Listar pedidos
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // Obtener pedido por ID
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }
    public List<DetallePedido> obtenerDetallesPorPedido(Long pedidoId) {
        return detallePedidoRepository.findAll().stream()
                .filter(d -> d.getPedido().getId().equals(pedidoId))
                .toList();
    }
    
    // Guardar pedido
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // Guardar un detalle de pedido
    public void guardarDetalle(DetallePedido detalle) {
        detallePedidoRepository.save(detalle);
    }

    // Eliminar un pedido
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    public void eliminarDetallesPorPedido(Long pedidoId) {
        List<DetallePedido> detalles = detallePedidoRepository.findAll().stream()
                .filter(d -> d.getPedido().getId().equals(pedidoId))
                .toList();
        detallePedidoRepository.deleteAll(detalles);
    }
    
    // Obtener todos los detalles de pedido
    public List<DetallePedido> listarDetalles() {
        return detallePedidoRepository.findAll();
    }
}
