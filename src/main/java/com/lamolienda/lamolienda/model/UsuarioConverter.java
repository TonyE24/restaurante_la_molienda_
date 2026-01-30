package com.lamolienda.lamolienda.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.lamolienda.lamolienda.service.UsuarioService;

@Component
public class UsuarioConverter implements Converter<String, Usuario> {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public Usuario convert(String source) {
        try {
            Long id = Long.parseLong(source);
            return usuarioService.listarUsuarios()
                                 .stream()
                                 .filter(u -> u.getId().equals(id))
                                 .findFirst()
                                 .orElse(null);
        } catch (NumberFormatException e) {
            return null; // o lanza una excepción custom si prefieres
        }
    }
}
