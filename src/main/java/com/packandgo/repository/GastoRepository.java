package com.packandgo.repository;

import com.packandgo.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByPresupuestoId(Long presupuestoId);

    List<Gasto> findByFechaGastoAfter(LocalDate fechaMinima);

    List<Gasto> findByFechaGastoBetween(LocalDate desde, LocalDate hasta);
}
