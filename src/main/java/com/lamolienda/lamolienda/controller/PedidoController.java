package com.lamolienda.lamolienda.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lamolienda.lamolienda.model.DetallePedido;
import com.lamolienda.lamolienda.model.Mesa;
import com.lamolienda.lamolienda.model.Mesa.Estado;
import com.lamolienda.lamolienda.model.Pedido;
import com.lamolienda.lamolienda.model.Platillo;
import com.lamolienda.lamolienda.model.Usuario;
import com.lamolienda.lamolienda.service.MesaService;
import com.lamolienda.lamolienda.service.PedidoService;
import com.lamolienda.lamolienda.service.PlatilloService;
import com.lamolienda.lamolienda.service.UsuarioService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private MesaService mesaService;

    @Autowired
    private PlatilloService platilloService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        model.addAttribute("contenido", "pedidos");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("mesas", mesaService.listarMesasDisponibles());
        model.addAttribute("meseros", usuarioService.listarMeseros());
        model.addAttribute("platillos", platilloService.listarPlatillosDisponibles());
        model.addAttribute("contenido", "form_pedido");
        return "layout";
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id).orElse(null);
        if (pedido == null) {
            return "redirect:/pedidos";
        }

        // ✅ Mantener la mesa original en el combo aunque esté ocupada
        List<Mesa> mesas = mesaService.listarMesasDisponibles();
        if (pedido.getMesa() != null && !mesas.contains(pedido.getMesa())) {
            mesas.add(pedido.getMesa());
        }

        // ✅ Mapear cantidades por platillo para mostrarlas en la vista
        List<DetallePedido> detalles = pedidoService.obtenerDetallesPorPedido(pedido.getId());
        Map<Long, Integer> cantidadesMap = detalles.stream()
                .collect(Collectors.toMap(
                        d -> d.getPlatillo().getId(),
                        DetallePedido::getCantidad
                ));

        model.addAttribute("pedido", pedido);
        model.addAttribute("mesas", mesas);
        model.addAttribute("meseros", usuarioService.listarMeseros());
        model.addAttribute("platillos", platilloService.listarPlatillosDisponibles());
        model.addAttribute("cantidadesMap", cantidadesMap);
        model.addAttribute("contenido", "form_pedido");
        return "layout";
    }

    @PostMapping("/guardar")
    public String guardarPedido(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("mesa.id") Long mesaId,
            @RequestParam("mesero.id") Long meseroId,
            @RequestParam("platilloIds") List<Long> platilloIds,
            @RequestParam("cantidades") List<String> cantidadesRaw,
            Model model) {

        Mesa mesa = mesaService.obtenerMesaPorId(mesaId).orElse(null);
        Usuario mesero = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getId().equals(meseroId))
                .findFirst()
                .orElse(null);

        if (mesa == null || mesero == null) {
            model.addAttribute("error", "Debes seleccionar una mesa y un mesero válidos.");
            model.addAttribute("pedido", new Pedido());
            model.addAttribute("mesas", mesaService.listarMesas());
            model.addAttribute("meseros", usuarioService.listarMeseros());
            model.addAttribute("platillos", platilloService.listarPlatillosDisponibles());
            model.addAttribute("contenido", "form_pedido");
            return "layout";
        }

        mesa.setEstado(Estado.OCUPADA);
        mesaService.guardarMesa(mesa);

        Pedido pedido = (id != null) ? pedidoService.obtenerPedidoPorId(id).orElse(new Pedido()) : new Pedido();
        pedido.setMesa(mesa);
        pedido.setMesero(mesero);

        Pedido pedidoGuardado = pedidoService.guardarPedido(pedido);

        pedidoService.eliminarDetallesPorPedido(pedidoGuardado.getId());

        for (int i = 0; i < platilloIds.size(); i++) {
            Platillo platillo = platilloService.obtenerPlatilloPorId(platilloIds.get(i)).orElse(null);
            String cantidadStr = cantidadesRaw.get(i);

            int cantidad = 0;
            try {
                cantidad = Integer.parseInt(cantidadStr);
            } catch (NumberFormatException e) {
                cantidad = 0;
            }

            if (platillo != null && cantidad > 0) {
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoGuardado);
                detalle.setPlatillo(platillo);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(platillo.getPrecio());
                pedidoService.guardarDetalle(detalle);
            }
        }

        return "redirect:/pedidos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(id).orElse(null);

        if (pedido != null && pedido.getMesa() != null) {
            Mesa mesa = pedido.getMesa();
            mesa.setEstado(Estado.LIBRE);
            mesaService.guardarMesa(mesa);
        }

        pedidoService.eliminarPedido(id);
        return "redirect:/pedidos";
    }
}
