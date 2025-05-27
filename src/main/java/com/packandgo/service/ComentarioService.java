package com.packandgo.service;

import com.packandgo.dto.ComentarioDTO;

import java.util.List;

public interface ComentarioService {
    ComentarioDTO crearComentario(String emailAutor, String contenido);
    ComentarioDTO editarComentario(String emailAutor, Long comentarioId, String nuevoContenido);
    List<ComentarioDTO> obtenerComentariosAprobados();
    List<ComentarioDTO> obtenerTodosComentarios();
    void aprobarComentario(Long comentarioId);
	void eliminarComentario(String emailSolicitante, Long comentarioId);
}
