package com.packandgo.service;

import com.packandgo.dto.UsuarioDTO;
import com.packandgo.enums.AuthProvider;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsuarioService {
    UserDetailsService userDetailsService();

    List<UsuarioDTO> buscarClientes(String nombre, String email, AuthProvider provider, Date fechaCreacion);

    void bloquearUsuario(Long id, boolean bloquear);
}
