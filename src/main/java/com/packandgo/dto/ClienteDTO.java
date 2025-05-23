package com.packandgo.dto;

import com.packandgo.enums.MetodoPago;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClienteDTO extends UsuarioDTO {
    private String apellido1;
    private String apellido2;
    private String dni;
    private String telefono;
    private String domicilio;
    private LocalDate fechaNacimiento;
    private MetodoPago metodoPago;
    private boolean notificaciones;
}
