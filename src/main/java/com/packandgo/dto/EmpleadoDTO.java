package com.packandgo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmpleadoDTO extends UsuarioDTO {
    private String dni;
    private String apellido1;
    private String apellido2;
    private String telefono;
    private String domicilio;
    private Double salario;
    private Date fechaContratacion;
    private Date fechaCese;
}

