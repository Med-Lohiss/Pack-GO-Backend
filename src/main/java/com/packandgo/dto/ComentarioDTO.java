package com.packandgo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComentarioDTO {
    private Long id;
    private String contenido;
    private Long autorId;
    private String autorNombre;
    private LocalDateTime fechaCreacion;
    private boolean aprobado;
}
