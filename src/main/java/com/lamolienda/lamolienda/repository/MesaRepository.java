package com.lamolienda.lamolienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lamolienda.lamolienda.model.Mesa;
import com.lamolienda.lamolienda.model.Mesa.Estado;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // ✅ Método para obtener solo mesas libres
    List<Mesa> findByEstado(Estado estado);
}
