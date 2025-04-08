package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "empleados")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Empleado extends Usuario {

    private static final long serialVersionUID = 1L;

    private String dni;
    private String apellido1;
    private String apellido2;
    private String telefono;
    private String domicilio;
    private Double salario;

    @Temporal(TemporalType.DATE)
    private Date fechaContratacion;

    @Temporal(TemporalType.DATE)
    private Date fechaCese;
}
