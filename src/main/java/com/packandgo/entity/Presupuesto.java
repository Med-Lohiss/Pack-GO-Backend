package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "presupuestos")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "viaje_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_presupuesto_viaje",
            foreignKeyDefinition = "FOREIGN KEY (viaje_id) REFERENCES viajes(id) ON DELETE CASCADE"
        )
    )
    private Viaje viaje;

    private BigDecimal totalEstimado;

    private BigDecimal totalGastado;

    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gasto> gastos;
}
