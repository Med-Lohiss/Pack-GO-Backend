package com.packandgo.service;

import com.packandgo.dto.NotificacionDTO;
import com.packandgo.entity.Notificacion;
import com.packandgo.entity.Usuario;
import com.packandgo.repository.NotificacionRepository;
import com.packandgo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public NotificacionDTO crearNotificacion(NotificacionDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .emailDestino(dto.getEmailDestino())
                .contenido(dto.getContenido())
                .leido(false)
                .fechaEnvio(LocalDateTime.now())
                .build();

        return convertirADTO(notificacionRepository.save(notificacion));
    }

    @Override
    public List<NotificacionDTO> obtenerNotificacionesPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return notificacionRepository.findByUsuario(usuario)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private NotificacionDTO convertirADTO(Notificacion n) {
        return NotificacionDTO.builder()
                .id(n.getId())
                .usuarioId(n.getUsuario().getId())
                .emailDestino(n.getEmailDestino())
                .contenido(n.getContenido())
                .leido(n.getLeido())
                .fechaEnvio(n.getFechaEnvio())
                .build();
    }
    
    @Override
    public void marcarComoLeido(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));

        if (!Boolean.TRUE.equals(notificacion.getLeido())) {
            notificacion.setLeido(true);
            notificacionRepository.save(notificacion);
        }
    }
}
