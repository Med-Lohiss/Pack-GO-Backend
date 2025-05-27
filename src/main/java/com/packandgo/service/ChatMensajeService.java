package com.packandgo.service;

import com.packandgo.dto.ChatMensajeDTO;

import java.util.List;

public interface ChatMensajeService {
    ChatMensajeDTO guardarMensaje(ChatMensajeDTO dto);
    List<ChatMensajeDTO> obtenerMensajesPorViaje(Long viajeId);
}
