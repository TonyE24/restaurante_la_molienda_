package com.lamolienda.lamolienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lamolienda.lamolienda.model.Platillo;

@Repository
public interface PlatilloRepository extends JpaRepository<Platillo, Long> {
    
    // Devuelve solo los platillos donde disponible = true
    List<Platillo> findByDisponibleTrue();
}
