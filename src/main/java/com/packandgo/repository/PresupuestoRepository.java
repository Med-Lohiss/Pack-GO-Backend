package com.packandgo.repository;

import com.packandgo.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    Optional<Presupuesto> findByViajeId(Long viajeId);
}
