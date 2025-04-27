package com.packandgo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ViajeDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String categoria;
    private boolean publico;
    private boolean compartido;
    private Long creadoPorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String imagenUrl;
}
