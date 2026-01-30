package com.lamolienda.lamolienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lamolienda.lamolienda.model.Inventario;
import com.lamolienda.lamolienda.repository.InventarioRepository;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }

    public Optional<Inventario> obtenerPorId(Long id) {
        return inventarioRepository.findById(id);
    }
}
