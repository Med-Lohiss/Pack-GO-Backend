package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "viajes")
public class Viaje {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String titulo;

	private String descripcion;

	private String ubicacion;

	private LocalDate fechaInicio;

	private LocalDate fechaFin;

	private String categoria;

	private boolean publico;

	@Column(name = "compartido")
	private boolean compartido = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creado_por")
	private Usuario creadoPor;

	@CreationTimestamp
	private LocalDateTime fechaCreacion;

	@UpdateTimestamp
	private LocalDateTime fechaModificacion;

	@Column(name = "imagen_url")
	private String imagenUrl;

	@OneToMany(mappedBy = "viaje", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Actividad> actividades;

}
