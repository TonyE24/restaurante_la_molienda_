package com.lamolienda.lamolienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lamolienda.lamolienda.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Puedes agregar búsquedas personalizadas después
}
