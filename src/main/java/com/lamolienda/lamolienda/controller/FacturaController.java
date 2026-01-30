package com.lamolienda.lamolienda.controller;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lamolienda.lamolienda.model.DetallePedido;
import com.lamolienda.lamolienda.model.Factura;
import com.lamolienda.lamolienda.model.Pedido;
import com.lamolienda.lamolienda.service.FacturaService;
import com.lamolienda.lamolienda.service.MesaService;
import com.lamolienda.lamolienda.service.PedidoService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/facturacion")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private MesaService mesaService;


    // Listar todas las facturas
    @GetMapping
    public String listarFacturas(Model model) {
        model.addAttribute("facturas", facturaService.listarFacturas());
        model.addAttribute("contenido", "facturas");
        return "layout";
    }

    // Mostrar formulario para facturar un pedido
    @GetMapping("/nuevo")
    public String nuevaFactura(@RequestParam(name = "pedidoId", required = false) Long pedidoId, Model model) {
        Factura factura = new Factura();

        if (pedidoId != null) {
            Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId).orElse(null);
            if (pedido != null) {
                factura.setPedido(pedido);
            }
        }

        model.addAttribute("factura", factura);

        List<Pedido> pedidosDisponibles = pedidoService.listarPedidos().stream()
                .filter(p -> !facturaService.existeFacturaParaPedido(p.getId()))
                .toList();

        model.addAttribute("pedidos", pedidosDisponibles);
        model.addAttribute("contenido", "form_factura");
        return "layout";
    }

    // Guardar la factura
    @PostMapping("/guardar")
public String guardarFactura(
        @RequestParam("pedido.id") Long pedidoId,
        @ModelAttribute Factura factura,
        Model model) {

    Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId).orElse(null);

    if (pedido == null) {
        model.addAttribute("error", "El pedido no existe.");
        model.addAttribute("factura", new Factura());
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        model.addAttribute("contenido", "form_factura");
        return "layout";
    }

    if (facturaService.existeFacturaParaPedido(pedido.getId())) {
        model.addAttribute("error", "Este pedido ya fue facturado.");
        model.addAttribute("factura", new Factura());
        model.addAttribute("pedidos", pedidoService.listarPedidos().stream()
                .filter(p -> !facturaService.existeFacturaParaPedido(p.getId()))
                .toList());
        model.addAttribute("contenido", "form_factura");
        return "layout";
    }

    factura.setPedido(pedido);
    facturaService.guardarFactura(factura);

    // ✅ Marcar pedido como facturado
    pedido.setEstado(Pedido.Estado.FACTURADO);
    pedidoService.guardarPedido(pedido);

    // ✅ Marcar mesa como LIBRE
    if (pedido.getMesa() != null) {
        pedido.getMesa().setEstado(com.lamolienda.lamolienda.model.Mesa.Estado.LIBRE);
        // Asegúrate de tener mesaService disponible si aún no está en este controlador
        mesaService.guardarMesa(pedido.getMesa());
    }

    return "redirect:/facturacion";
}


    // Ver detalle de factura tipo recibo
    @GetMapping("/detalle/{id}")
    public String verDetalleFactura(@PathVariable Long id, Model model) {
        Factura factura = facturaService.obtenerFacturaPorId(id);
        if (factura == null) {
            return "redirect:/facturacion";
        }

        List<DetallePedido> detalles = facturaService.obtenerDetallesPorFactura(factura);
        model.addAttribute("factura", factura);
        model.addAttribute("detalles", detalles);
        model.addAttribute("contenido", "detalle_factura");
        return "layout";
    }

    // Eliminar factura
    @GetMapping("/eliminar/{id}")
    public String eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return "redirect:/facturacion";
    }

    @GetMapping("/resumen")
    public String mostrarResumen(Model model) {
    Map<LocalDate, Double> resumen = facturaService.resumenPorDia();
    model.addAttribute("resumen", resumen);
    model.addAttribute("modo", "dia");
    model.addAttribute("contenido", "resumen_facturacion");
    return "layout";
}   

    @GetMapping("/resumen-mes")
    public String mostrarResumenMensual(Model model) {
    Map<YearMonth, Double> resumen = facturaService.resumenPorMes();
    model.addAttribute("resumen", resumen);
    model.addAttribute("modo", "mes");
    model.addAttribute("contenido", "resumen_facturacion");
    return "layout";
}
@GetMapping("/pdf/{id}")
public void generarFacturaPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
    Factura factura = facturaService.obtenerFacturaPorId(id);
    if (factura == null) {
        response.sendRedirect("/facturacion");
        return;
    }

    List<DetallePedido> detalles = facturaService.obtenerDetallesPorFactura(factura);

    response.setContentType("application/pdf");
    String headerKey = "Content-Disposition";
    String headerValue = "attachment; filename=factura_" + factura.getId() + ".pdf";
    response.setHeader(headerKey, headerValue);

    Document document = new Document(PageSize.A5);
    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();

    // Estilos
    Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(249, 179, 0)); // #f9b300
    Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
    Font textFont = new Font(Font.HELVETICA, 12);
    Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
    Color headerColor = new Color(21, 128, 61); // #15803d

    // Título
    Paragraph title = new Paragraph("La Molienda de Don Toño\nFactura #" + factura.getId(), titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(20);
    document.add(title);

    // Info general
    Paragraph info = new Paragraph();
    info.setSpacingAfter(10);
    info.add(new Phrase("Fecha: ", boldFont));
    info.add(new Phrase(factura.getFecha().toString() + "\n", textFont));
    info.add(new Phrase("Mesa: ", boldFont));
    info.add(new Phrase(factura.getPedido().getMesa().getNumero() + "\n", textFont));
    info.add(new Phrase("Mesero: ", boldFont));
    info.add(new Phrase(factura.getPedido().getMesero().getNombre() + "\n", textFont));
    document.add(info);

    // Tabla
    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1f, 2f, 2f});
    table.setSpacingBefore(10);
    table.setSpacingAfter(15);

    for (String header : List.of("Platillo", "Cant.", "P. Unit", "Subtotal")) {
        PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
        cell.setBackgroundColor(headerColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    for (DetallePedido d : detalles) {
        PdfPCell c1 = new PdfPCell(new Phrase(d.getPlatillo().getNombre(), textFont));
        PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), textFont));
        PdfPCell c3 = new PdfPCell(new Phrase("$" + String.format("%.2f", d.getPrecioUnitario()), textFont));
        PdfPCell c4 = new PdfPCell(new Phrase("$" + String.format("%.2f", d.getCantidad() * d.getPrecioUnitario()), textFont));

        c1.setPadding(5); c2.setPadding(5); c3.setPadding(5); c4.setPadding(5);
        table.addCell(c1); table.addCell(c2); table.addCell(c3); table.addCell(c4);
    }

    document.add(table);

    // Totales
    Paragraph totales = new Paragraph();
    totales.setAlignment(Element.ALIGN_RIGHT);
    totales.setSpacingBefore(10);
    totales.add(new Phrase("IVA: ", boldFont));
    totales.add(new Phrase("$" + String.format("%.2f", factura.getIva()) + "\n", textFont));
    totales.add(new Phrase("TOTAL: ", boldFont));
    totales.add(new Phrase("$" + String.format("%.2f", factura.getTotal()) + "\n", textFont));
    totales.add(new Phrase("Método de Pago: ", boldFont));
    totales.add(new Phrase(factura.getMetodoPago().toString(), textFont));

    document.add(totales);

    document.close();
}
}