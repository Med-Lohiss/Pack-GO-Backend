package com.packandgo.service.auth;

import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.dto.UsuarioDTO;

public interface AuthService {
    UsuarioDTO crearUsuario(SolicitudRegistro solicitudRegistro);
    boolean existeUsuarioPorEmail(String email);
    String encriptarPassword(String password);
}

