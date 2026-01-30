package com.lamolienda.lamolienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lamolienda.lamolienda.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}
