package com.packandgo.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {
    private Long id;
    private String cif;
    private String denominacionSocial;
    private String domicilio;
    private Date fechaConstitucion;
    private String direccionWeb;
    private String telefono;
    private String emailContacto;
}
