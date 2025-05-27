package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_reportante_id", nullable = false)
    private Usuario usuarioReportante;

    @Column(nullable = true, length = 1000)
    private String contenido;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte = LocalDateTime.now();
}
