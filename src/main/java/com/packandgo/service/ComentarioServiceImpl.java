package com.packandgo.service;

import com.packandgo.dto.ComentarioDTO;
import com.packandgo.entity.Comentario;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.ComentarioRepository;
import com.packandgo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public ComentarioDTO crearComentario(String emailAutor, String contenido) {
        Usuario autor = usuarioRepository.findByEmail(emailAutor)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comentario comentario = Comentario.builder()
                .contenido(contenido)
                .autor(autor)
                .fechaCreacion(LocalDateTime.now())
                .aprobado(false)
                .build();

        Comentario guardado = comentarioRepository.save(comentario);
        return mapToDTO(guardado);
    }

    @Override
    public ComentarioDTO editarComentario(String emailAutor, Long comentarioId, String nuevoContenido) {
        Usuario autor = usuarioRepository.findByEmail(emailAutor)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (autor.getRolUsuario() != RolUsuario.EMPLEADO) {
            throw new RuntimeException("Solo los empleados pueden editar comentarios.");
        }

        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        comentario.setContenido(nuevoContenido);
        comentario.setFechaCreacion(LocalDateTime.now());
        comentario.setAprobado(false);

        Comentario actualizado = comentarioRepository.save(comentario);
        return mapToDTO(actualizado);
    }

    @Override
    public List<ComentarioDTO> obtenerComentariosAprobados() {
        return comentarioRepository.findByAprobadoTrueOrderByFechaCreacionDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComentarioDTO> obtenerTodosComentarios() {
        return comentarioRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void aprobarComentario(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        comentario.setAprobado(true);
        comentarioRepository.save(comentario);
    }

    @Override
    public void eliminarComentario(String emailSolicitante, Long comentarioId) {
        Usuario usuario = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRolUsuario() != RolUsuario.EMPLEADO) {
            throw new RuntimeException("Solo los empleados pueden eliminar comentarios.");
        }

        comentarioRepository.deleteById(comentarioId);
    }

    private ComentarioDTO mapToDTO(Comentario comentario) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(comentario.getId());
        dto.setContenido(comentario.getContenido());
        dto.setAutorId(comentario.getAutor().getId());
        dto.setAutorNombre(comentario.getAutor().getNombre());
        dto.setFechaCreacion(comentario.getFechaCreacion());
        dto.setAprobado(comentario.isAprobado());
        return dto;
    }
}
