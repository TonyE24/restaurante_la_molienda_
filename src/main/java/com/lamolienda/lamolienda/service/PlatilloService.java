package com.lamolienda.lamolienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lamolienda.lamolienda.model.Platillo;
import com.lamolienda.lamolienda.repository.PlatilloRepository;

@Service
public class PlatilloService {

    @Autowired
    private PlatilloRepository platilloRepository;

    public List<Platillo> listarPlatillos() {
        return platilloRepository.findAll();
    }

    // Nuevo método para obtener solo los platillos disponibles
    public List<Platillo> listarPlatillosDisponibles() {
        return platilloRepository.findByDisponibleTrue();
    }

    public Optional<Platillo> obtenerPlatilloPorId(Long id) {
        return platilloRepository.findById(id);
    }

    public void guardarPlatillo(Platillo platillo) {
        platilloRepository.save(platillo);
    }

    public void eliminarPlatillo(Long id) {
        platilloRepository.deleteById(id);
    }
}
