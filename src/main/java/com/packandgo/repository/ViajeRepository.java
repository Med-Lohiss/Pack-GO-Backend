package com.packandgo.repository;

import com.packandgo.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViajeRepository extends JpaRepository<Viaje, Long> {

	List<Viaje> findByCreadoPorId(Long usuarioId);

	List<Viaje> findByCompartidoTrue();
}
