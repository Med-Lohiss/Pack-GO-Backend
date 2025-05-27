package com.packandgo.repository;

import com.packandgo.entity.Actividad;
import com.packandgo.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {
	
    List<Actividad> findByViaje(Viaje viaje);
}
