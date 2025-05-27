package com.packandgo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PresupuestoDTO {
    private Long id;
    private Long viajeId;
    private BigDecimal totalEstimado;
    private BigDecimal totalGastado;
    private LocalDateTime fechaActualizacion;
}
