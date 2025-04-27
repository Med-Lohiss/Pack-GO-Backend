package com.packandgo.service;

import com.packandgo.dto.ViajeDTO;
import com.packandgo.entity.Usuario;

import java.util.List;

public interface ViajeService {
	// Uso cliente
	ViajeDTO crearViaje(String emailUsuario, ViajeDTO viajeDTO);

	List<ViajeDTO> listarViajesUsuario(String emailUsuario);

	ViajeDTO editarViaje(String emailUsuario, Long viajeId, ViajeDTO viajeDTO);

	void eliminarViaje(String emailUsuario, Long viajeId);

	ViajeDTO obtenerViajePorId(String emailUsuario, Long viajeId);

	// Uso empleado
	ViajeDTO crearViajeComoEmpleado(String emailEmpleado, ViajeDTO dto);

	List<ViajeDTO> listarTodosLosViajes();

	ViajeDTO obtenerViajePorIdComoEmpleado(Long viajeId);

	ViajeDTO editarViajeComoEmpleado(String emailEmpleado, Long viajeId, ViajeDTO dto);

	void eliminarViajeComoEmpleado(Long viajeId);

	List<ViajeDTO> listarViajesCompartidos();

	Usuario obtenerUsuarioPorId(Long id);
}
