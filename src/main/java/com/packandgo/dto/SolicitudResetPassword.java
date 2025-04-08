package com.packandgo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudResetPassword {
    private String email;
    private String codigo;
    private String nuevaPassword;
}
