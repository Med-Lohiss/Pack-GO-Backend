package com.packandgo.repository;

import com.packandgo.entity.Empleado;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

	Optional<Empleado> findByEmail(String email);

}
