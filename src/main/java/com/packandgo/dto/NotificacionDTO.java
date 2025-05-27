package com.packandgo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDTO {
    private Long id;
    private Long usuarioId;
    private String emailDestino;
    private String contenido;
    private Boolean leido;
    private LocalDateTime fechaEnvio;
}
