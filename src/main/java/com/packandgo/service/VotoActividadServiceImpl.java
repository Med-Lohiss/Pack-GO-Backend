package com.packandgo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.packandgo.dto.VotoActividadDTO;
import com.packandgo.entity.Actividad;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.VotoActividad;
import com.packandgo.repository.ActividadRepository;
import com.packandgo.repository.UsuarioRepository;
import com.packandgo.repository.VotoActividadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotoActividadServiceImpl implements VotoActividadService {

    private final VotoActividadRepository votoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ActividadRepository actividadRepo;

    @Override
    public VotoActividadDTO votarActividad(Long usuarioId, Long actividadId, Double valor) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Actividad actividad = actividadRepo.findById(actividadId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

        VotoActividad voto = votoRepo.findByUsuarioAndActividad(usuario, actividad)
                .orElse(VotoActividad.builder()
                        .usuario(usuario)
                        .actividad(actividad)
                        .build());

        voto.setValor(valor);
        VotoActividad guardado = votoRepo.save(voto);

        return VotoActividadDTO.builder()
                .id(guardado.getId())
                .usuarioId(usuario.getId())
                .actividadId(actividad.getId())
                .valor(guardado.getValor())
                .build();
    }

    @Override
    public Double obtenerPromedioVotos(Long actividadId) {
        Actividad actividad = actividadRepo.findById(actividadId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
        List<VotoActividad> votos = votoRepo.findByActividad(actividad);
        return votos.isEmpty() ? 0.0 :
               votos.stream().mapToDouble(VotoActividad::getValor).average().orElse(0.0);
    }

    @Override
    public Optional<VotoActividadDTO> obtenerVotoDeUsuario(Long usuarioId, Long actividadId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Actividad actividad = actividadRepo.findById(actividadId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
        return votoRepo.findByUsuarioAndActividad(usuario, actividad)
                .map(v -> VotoActividadDTO.builder()
                        .id(v.getId())
                        .usuarioId(usuarioId)
                        .actividadId(actividadId)
                        .valor(v.getValor())
                        .build());
    }
}
