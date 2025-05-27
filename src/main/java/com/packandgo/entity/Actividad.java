package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "actividades")
public class Actividad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	private String descripcion;

	private LocalDate fecha;

	private LocalTime hora;

	private Double precio;

	@Column(name = "tipo_actividad")
	private String tipoActividad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "viaje_id", nullable = false, foreignKey = @ForeignKey(name = "fk_actividad_viaje", foreignKeyDefinition = "FOREIGN KEY (viaje_id) REFERENCES viajes(id) ON DELETE CASCADE"))
	private Viaje viaje;
}
