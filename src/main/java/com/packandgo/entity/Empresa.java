package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String cif;

	private String denominacionSocial;

	private String domicilio;

	@Temporal(TemporalType.DATE)
	private Date fechaConstitucion;

	private String direccionWeb;

	private String telefono;

	private String emailContacto;
}
