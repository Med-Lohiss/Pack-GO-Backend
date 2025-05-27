package com.packandgo.service;

import com.packandgo.dto.ChatMensajeDTO;
import com.packandgo.entity.ChatMensaje;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.Viaje;
import com.packandgo.repository.ChatMensajeRepository;
import com.packandgo.repository.UsuarioRepository;
import com.packandgo.repository.ViajeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMensajeServiceImpl implements ChatMensajeService {

    private final ChatMensajeRepository chatMensajeRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public ChatMensajeDTO guardarMensaje(ChatMensajeDTO dto) {
        Viaje viaje = viajeRepository.findById(dto.getViajeId())
                .orElseThrow(() -> new EntityNotFoundException("Viaje no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        ChatMensaje mensaje = ChatMensaje.builder()
                .viaje(viaje)
                .usuario(usuario)
                .mensaje(dto.getMensaje())
                .fechaEnvio(LocalDateTime.now())
                .build();

        ChatMensaje guardado = chatMensajeRepository.save(mensaje);

        return toDTO(guardado);
    }

    @Override
    public List<ChatMensajeDTO> obtenerMensajesPorViaje(Long viajeId) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new EntityNotFoundException("Viaje no encontrado"));

        return chatMensajeRepository.findByViajeOrderByFechaEnvioAsc(viaje)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ChatMensajeDTO toDTO(ChatMensaje mensaje) {
        ChatMensajeDTO dto = new ChatMensajeDTO();
        dto.setId(mensaje.getId());
        dto.setViajeId(mensaje.getViaje().getId());
        dto.setUsuarioId(mensaje.getUsuario().getId());
        dto.setNombreUsuario(mensaje.getUsuario().getNombre());
        dto.setMensaje(mensaje.getMensaje());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        return dto;
    }
}

