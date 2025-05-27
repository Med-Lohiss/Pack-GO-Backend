package com.packandgo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GastoDTO {
    private Long id;
    private Long presupuestoId;
    private String concepto;
    private BigDecimal cantidad;
    private String pagadoPor;
    private LocalDate fechaGasto;
}
