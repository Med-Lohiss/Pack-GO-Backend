package com.packandgo.dto;

import com.packandgo.enums.RolUsuario;
import com.packandgo.enums.MetodoPago;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SolicitudRegistro {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Debe ser un correo electrónico válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    @NotNull(message = "El rol de usuario es obligatorio")
    private RolUsuario rolUsuario;

    // Datos específicos de Cliente
    private String apellido1;
    private String apellido2;
    private String dni;
    private String telefono;
    private String domicilio;
    private Date fechaNacimiento;
    private MetodoPago metodoPago;
    private Boolean notificaciones = true;

    // Datos específicos de Empleado
    private Double salario;
    private Date fechaContratacion;
    private Date fechaCese;
}

