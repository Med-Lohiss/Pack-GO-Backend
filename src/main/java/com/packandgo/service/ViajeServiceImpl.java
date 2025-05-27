package com.packandgo.service;

import com.packandgo.dto.ViajeDTO;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.Viaje;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.UsuarioRepository;
import com.packandgo.repository.ViajeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViajeServiceImpl implements ViajeService {

	private final ViajeRepository viajeRepository;
	private final UsuarioRepository usuarioRepository;
	private final InvitacionService invitacionService;

	// CLIENTE

	@Override
	public ViajeDTO crearViaje(String emailUsuario, ViajeDTO dto) {
		Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + emailUsuario));

		Viaje viaje = mapFromDTO(dto);
		viaje.setCreadoPor(usuario);

		Viaje guardado = viajeRepository.save(viaje);
		return mapToDTO(guardado);
	}

	@Override
	public List<ViajeDTO> listarViajesUsuario(String emailUsuario) {
		Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + emailUsuario));

		return viajeRepository.findByCreadoPorId(usuario.getId()).stream().map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public ViajeDTO obtenerViajePorId(String emailUsuario, Long viajeId) {
		Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + emailUsuario));

		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + viajeId));

		// Aquí la validación usando usuarioTieneAccesoAViaje:
		if (!invitacionService.usuarioTieneAccesoAViaje(usuario, viaje)) {
			throw new RuntimeException("No tienes permisos para ver este viaje.");
		}

		return mapToDTO(viaje);
	}

	@Override
	public ViajeDTO editarViaje(String emailUsuario, Long viajeId, ViajeDTO dto) {
		Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + emailUsuario));

		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + viajeId));

		if (!viaje.getCreadoPor().getId().equals(usuario.getId())) {
			throw new RuntimeException("No tienes permisos para editar este viaje.");
		}

		actualizarViajeDesdeDTO(viaje, dto);
		Viaje actualizado = viajeRepository.save(viaje);
		return mapToDTO(actualizado);
	}

	@Override
	public void eliminarViaje(String emailUsuario, Long viajeId) {
		Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + emailUsuario));

		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + viajeId));

		if (!viaje.getCreadoPor().getId().equals(usuario.getId())) {
			throw new RuntimeException("No tienes permisos para eliminar este viaje.");
		}

		viajeRepository.delete(viaje);
	}

	// EMPLEADO

	@Override
	public ViajeDTO crearViajeComoEmpleado(String emailEmpleado, ViajeDTO dto) {
		Usuario empleado = usuarioRepository.findByEmail(emailEmpleado)
				.orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + emailEmpleado));

		Viaje viaje = mapFromDTO(dto);
		viaje.setCreadoPor(empleado);

		Viaje guardado = viajeRepository.save(viaje);
		return mapToDTO(guardado);
	}

	@Override
	public List<ViajeDTO> listarTodosLosViajes() {
		return viajeRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public ViajeDTO obtenerViajePorIdComoEmpleado(Long viajeId) {
		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + viajeId));
		return mapToDTO(viaje);
	}

	@Override
	public ViajeDTO editarViajeComoEmpleado(String emailEmpleado, Long viajeId, ViajeDTO dto) {

		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + viajeId));

		Usuario creador = viaje.getCreadoPor();
		if (creador != null && creador.getRolUsuario() == RolUsuario.CLIENTE) {
			if (!viaje.isPublico() && dto.isCompartido()) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
						"No se puede compartir un viaje que no es público.");
			}
		}

		actualizarViajeDesdeDTO(viaje, dto);

		Viaje actualizado = viajeRepository.save(viaje);
		return mapToDTO(actualizado);
	}

	@Override
	public void eliminarViajeComoEmpleado(Long viajeId) {
		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + viajeId));
		viajeRepository.delete(viaje);
	}


	private ViajeDTO mapToDTO(Viaje viaje) {
		ViajeDTO dto = new ViajeDTO();
		dto.setId(viaje.getId());
		dto.setTitulo(viaje.getTitulo());
		dto.setDescripcion(viaje.getDescripcion());
		dto.setUbicacion(viaje.getUbicacion());
		dto.setFechaInicio(viaje.getFechaInicio());
		dto.setFechaFin(viaje.getFechaFin());
		dto.setCategoria(viaje.getCategoria());
		dto.setPublico(viaje.isPublico());
		dto.setCompartido(viaje.isCompartido());
		dto.setCreadoPorId(viaje.getCreadoPor().getId());
		dto.setFechaCreacion(viaje.getFechaCreacion());
		dto.setFechaModificacion(viaje.getFechaModificacion());
		dto.setImagenUrl(viaje.getImagenUrl());
		return dto;
	}

	private Viaje mapFromDTO(ViajeDTO dto) {
		Viaje viaje = new Viaje();
		viaje.setTitulo(dto.getTitulo());
		viaje.setDescripcion(dto.getDescripcion());
		viaje.setUbicacion(dto.getUbicacion());
		viaje.setFechaInicio(dto.getFechaInicio());
		viaje.setFechaFin(dto.getFechaFin());
		viaje.setCategoria(dto.getCategoria());
		viaje.setPublico(dto.isPublico());
		viaje.setCompartido(dto.isCompartido());
		viaje.setImagenUrl(dto.getImagenUrl());
		return viaje;
	}

	private void actualizarViajeDesdeDTO(Viaje viaje, ViajeDTO dto) {
		viaje.setTitulo(dto.getTitulo());
		viaje.setDescripcion(dto.getDescripcion());
		viaje.setUbicacion(dto.getUbicacion());
		viaje.setFechaInicio(dto.getFechaInicio());
		viaje.setFechaFin(dto.getFechaFin());
		viaje.setCategoria(dto.getCategoria());
		viaje.setPublico(dto.isPublico());
		viaje.setCompartido(dto.isCompartido());
		viaje.setImagenUrl(dto.getImagenUrl());
	}

	public Usuario obtenerUsuarioPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
	}

	@Override
	public List<ViajeDTO> listarViajesCompartidos() {
		return viajeRepository.findByCompartidoTrue().stream().map(this::mapToDTO).collect(Collectors.toList());
	}
}
