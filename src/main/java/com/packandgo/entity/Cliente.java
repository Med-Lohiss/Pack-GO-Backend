package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import com.packandgo.enums.MetodoPago;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Cliente extends Usuario {

    private static final long serialVersionUID = 1L;

    @Column(nullable = true)
    private String apellido1;
    
    @Column(nullable = true)
    private String apellido2;
    
    @Column(nullable = true)
    private String dni;
    
    @Column(nullable = true)
    private String telefono;
    
    @Column(nullable = true)
    private String domicilio;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MetodoPago metodoPago;

    @Column(nullable = true)
    private boolean notificaciones = true;
}
