package com.packandgo.dto;

import com.packandgo.enums.EstadoInvitacion;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitacionDTO {
    private Long id;
    private Long viajeId;
    private Long usuarioInvitadoId;
    private String emailInvitado;
    private String token;
    private EstadoInvitacion estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaAceptacion;
}
