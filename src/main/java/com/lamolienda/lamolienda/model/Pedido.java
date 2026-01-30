package com.lamolienda.lamolienda.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "mesero_id")
    private Usuario mesero;

    public enum Estado {
        EN_PROCESO,
        EN_COCINA,
        ENTREGADO,
        FACTURADO
    }

    public Pedido() {
        this.fecha = LocalDateTime.now();
        this.estado = Estado.EN_PROCESO;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Usuario getMesero() {
        return mesero;
    }

    public void setMesero(Usuario mesero) {
        this.mesero = mesero;
    }
}
