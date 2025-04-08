package com.packandgo.dto;

import lombok.Data;

@Data
public class FiltroEmpleadoDTO {
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String dni;
    private String email;
    private String telefono;
    private String domicilio;
    private String ordenarPor; // "salario", "fechaContratacion", etc.
    private String orden; // "asc" o "desc"
}
