package com.packandgo.service;

import com.packandgo.dto.NotificacionDTO;

import java.util.List;

public interface NotificacionService {
    NotificacionDTO crearNotificacion(NotificacionDTO dto);
    List<NotificacionDTO> obtenerNotificacionesPorUsuario(Long usuarioId);
    void marcarComoLeido(Long id);
}
