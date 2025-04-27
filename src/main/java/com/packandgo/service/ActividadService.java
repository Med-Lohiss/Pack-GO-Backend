package com.packandgo.service;

import com.packandgo.dto.ActividadDTO;

import java.util.List;

public interface ActividadService {

	ActividadDTO crearActividad(ActividadDTO actividadDTO);

	List<ActividadDTO> obtenerActividadesPorViaje(Long viajeId);

	ActividadDTO obtenerActividadPorId(Long id);

	void eliminarActividad(Long id);

	ActividadDTO actualizarActividad(Long id, ActividadDTO actividadDTO);
}
