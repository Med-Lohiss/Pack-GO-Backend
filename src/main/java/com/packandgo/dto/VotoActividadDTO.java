package com.packandgo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoActividadDTO {
    private Long id;
    private Long usuarioId;
    private Long actividadId;
    private Double valor;
}
