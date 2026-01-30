package com.lamolienda.lamolienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lamolienda.lamolienda.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Ya puedes buscar por ID, listar, guardar, etc.
}
