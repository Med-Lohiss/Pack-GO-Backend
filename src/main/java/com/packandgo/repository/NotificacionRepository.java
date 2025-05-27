package com.packandgo.repository;

import com.packandgo.entity.Notificacion;
import com.packandgo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuario(Usuario usuario);
}

