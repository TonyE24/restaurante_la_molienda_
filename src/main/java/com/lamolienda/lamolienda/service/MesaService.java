package com.lamolienda.lamolienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lamolienda.lamolienda.model.Mesa;
import com.lamolienda.lamolienda.model.Mesa.Estado;
import com.lamolienda.lamolienda.repository.MesaRepository;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    // ✅ Nuevo método para listar solo mesas LIBRES
    public List<Mesa> listarMesasDisponibles() {
        return mesaRepository.findByEstado(Estado.LIBRE);
    }

    public Optional<Mesa> obtenerMesaPorId(Long id) {
        return mesaRepository.findById(id);
    }

    public void guardarMesa(Mesa mesa) {
        mesaRepository.save(mesa);
    }

    public void eliminarMesa(Long id) {
        mesaRepository.deleteById(id);
    }
}
