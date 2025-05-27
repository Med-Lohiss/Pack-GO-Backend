package com.packandgo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMensajeDTO {
    private Long id;
    private Long viajeId;
    private Long usuarioId;
    private String nombreUsuario;
    private String mensaje;
    private LocalDateTime fechaEnvio;
}
