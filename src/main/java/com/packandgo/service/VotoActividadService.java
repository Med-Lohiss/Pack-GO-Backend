package com.packandgo.service;

import java.util.Optional;

import com.packandgo.dto.VotoActividadDTO;

public interface VotoActividadService {
    VotoActividadDTO votarActividad(Long usuarioId, Long actividadId, Double valor);
    Double obtenerPromedioVotos(Long actividadId);
    Optional<VotoActividadDTO> obtenerVotoDeUsuario(Long usuarioId, Long actividadId);
}
