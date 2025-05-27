package com.packandgo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReporteDTO {
    private Long id;
    private Long usuarioReportanteId;
    private String nombreUsuarioReportante;
    private String contenido;
    private String motivo;
    private LocalDateTime fechaReporte;
}
