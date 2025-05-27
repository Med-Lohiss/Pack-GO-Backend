package com.packandgo.repository;

import com.packandgo.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByAprobadoTrueOrderByFechaCreacionDesc();
    List<Comentario> findAllByOrderByFechaCreacionDesc();
}
