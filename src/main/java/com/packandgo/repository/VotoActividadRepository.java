package com.packandgo.repository;

import com.packandgo.entity.VotoActividad;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface VotoActividadRepository extends JpaRepository<VotoActividad, Long> {
    Optional<VotoActividad> findByUsuarioAndActividad(Usuario usuario, Actividad actividad);
    List<VotoActividad> findByActividad(Actividad actividad);
}
