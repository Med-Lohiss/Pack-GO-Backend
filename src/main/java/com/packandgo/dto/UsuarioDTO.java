package com.packandgo.dto;

import com.packandgo.enums.AuthProvider;
import com.packandgo.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private RolUsuario rolUsuario;
    private AuthProvider provider;
    private boolean cuentaBloqueada;
    private Date fechaCreacion;
    private Date fechaBaja;
}
