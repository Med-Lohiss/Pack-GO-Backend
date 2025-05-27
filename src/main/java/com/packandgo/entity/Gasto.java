package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    private String concepto;

    private BigDecimal cantidad;

    @Column(name = "pagado_por", nullable = false)
    private String pagadoPor;

    private LocalDate fechaGasto;
}

