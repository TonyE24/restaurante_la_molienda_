package com.lamolienda.lamolienda.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lamolienda.lamolienda.model.Usuario;
import com.lamolienda.lamolienda.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Listar solo usuarios con rol MESERO
    public List<Usuario> listarMeseros() {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getRol() == Usuario.Rol.MESERO)
                .collect(Collectors.toList());
    }

    // Guardar o actualizar usuario
    public void guardarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    // Eliminar usuario por ID
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
