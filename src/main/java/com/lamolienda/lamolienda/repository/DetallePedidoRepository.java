package com.lamolienda.lamolienda.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lamolienda.lamolienda.model.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT d.platillo.nombre, SUM(d.cantidad) " +
           "FROM DetallePedido d " +
           "GROUP BY d.platillo.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> topPlatillosMasVendidos(Pageable pageable);
}
