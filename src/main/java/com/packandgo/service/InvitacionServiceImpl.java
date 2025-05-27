package com.packandgo.service;

import com.packandgo.dto.InvitacionDTO;
import com.packandgo.dto.NotificacionDTO;
import com.packandgo.dto.ViajeDTO;
import com.packandgo.entity.Invitacion;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.Viaje;
import com.packandgo.enums.EstadoInvitacion;
import com.packandgo.repository.InvitacionRepository;
import com.packandgo.repository.UsuarioRepository;
import com.packandgo.repository.ViajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitacionServiceImpl implements InvitacionService {

	private final InvitacionRepository invitacionRepository;
	private final ViajeRepository viajeRepository;
	private final UsuarioRepository usuarioRepository;
	private final EmailService emailService;
	private final NotificacionService notificacionService;

	@Override
	public InvitacionDTO crearInvitacion(InvitacionDTO dto) {
		Viaje viaje = viajeRepository.findById(dto.getViajeId())
				.orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

		Usuario usuarioInvitado = null;
		if (dto.getUsuarioInvitadoId() != null) {
			usuarioInvitado = usuarioRepository.findById(dto.getUsuarioInvitadoId())
					.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		}

		Invitacion invitacion = Invitacion.builder().viaje(viaje).emailInvitado(dto.getEmailInvitado())
				.usuarioInvitado(usuarioInvitado).token(UUID.randomUUID().toString()).estado(EstadoInvitacion.PENDIENTE)
				.fechaEnvio(LocalDateTime.now()).build();

		return convertirADTO(invitacionRepository.save(invitacion));
	}

	@Override
	public InvitacionDTO aceptarInvitacion(String token, Usuario usuario) {
		Invitacion invitacion = invitacionRepository.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Invitación no encontrada"));

		if (!EstadoInvitacion.PENDIENTE.equals(invitacion.getEstado())) {
			throw new IllegalStateException("La invitación ya fue aceptada o está expirada.");
		}

		aceptarInvitacionesPendientes(invitacion.getEmailInvitado(), usuario);

		Invitacion actualizada = invitacionRepository.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Invitación no encontrada después de actualización"));

		return convertirADTO(actualizada);
	}

	@Override
	public InvitacionDTO obtenerPorToken(String token) {
		Invitacion invitacion = invitacionRepository.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Invitación no encontrada"));

		if (EstadoInvitacion.EXPIRADA.equals(invitacion.getEstado())) {
			throw new IllegalArgumentException("Invitación expirada");
		}

		return convertirADTO(invitacion);
	}

	@Override
	public List<Viaje> obtenerViajesCompartidos(Usuario usuario) {
		return invitacionRepository.findAllByUsuarioInvitadoAndEstado(usuario, EstadoInvitacion.ACEPTADA).stream()
				.map(Invitacion::getViaje).collect(Collectors.toList());
	}

	private InvitacionDTO convertirADTO(Invitacion invitacion) {
		return InvitacionDTO.builder().id(invitacion.getId()).viajeId(invitacion.getViaje().getId())
				.usuarioInvitadoId(
						invitacion.getUsuarioInvitado() != null ? invitacion.getUsuarioInvitado().getId() : null)
				.emailInvitado(invitacion.getEmailInvitado()).token(invitacion.getToken())
				.estado(invitacion.getEstado()).fechaEnvio(invitacion.getFechaEnvio())
				.fechaAceptacion(invitacion.getFechaAceptacion()).build();
	}

	@Override
	public ViajeDTO convertirAViajeDTO(Viaje viaje) {
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
		dto.setCreadoPorId(viaje.getCreadoPor() != null ? viaje.getCreadoPor().getId() : null);
		dto.setFechaCreacion(viaje.getFechaCreacion());
		dto.setFechaModificacion(viaje.getFechaModificacion());
		dto.setImagenUrl(viaje.getImagenUrl());
		return dto;
	}

	@Override
	public ViajeDTO obtenerViajePorToken(String token) {
		Invitacion invitacion = invitacionRepository.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Invitación no encontrada"));

		if (!EstadoInvitacion.ACEPTADA.equals(invitacion.getEstado())) {
			throw new IllegalArgumentException("Invitación no ha sido aceptada");
		}

		return convertirAViajeDTO(invitacion.getViaje());
	}

	public boolean usuarioTieneAccesoAViaje(Usuario usuario, Viaje viaje) {
		if (viaje.getCreadoPor().getId().equals(usuario.getId())) {
			return true;
		}

		return invitacionRepository.findAllByUsuarioInvitadoAndEstado(usuario, EstadoInvitacion.ACEPTADA).stream()
				.anyMatch(invitacion -> invitacion.getViaje().getId().equals(viaje.getId()));
	}

	@Override
	public ViajeDTO aceptarInvitacionSinAuth(String token) {
		Invitacion invitacion = invitacionRepository.findByToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Invitación no encontrada"));

		if (invitacion.getEstado() != EstadoInvitacion.PENDIENTE) {
			throw new IllegalStateException("La invitación ya fue aceptada o está expirada.");
		}

		Optional<Usuario> posibleUsuario = usuarioRepository.findByEmail(invitacion.getEmailInvitado());

		if (posibleUsuario.isEmpty()) {
			throw new SecurityException("El usuario aún no está registrado.");
		}

		Usuario usuario = posibleUsuario.get();

		aceptarInvitacionesPendientes(usuario.getEmail(), usuario);

		return convertirAViajeDTO(invitacion.getViaje());
	}

	@Override
	public void aceptarInvitacionesPendientes(String email, Usuario usuario) {
	    List<Invitacion> invitaciones = invitacionRepository.findAllByEmailInvitadoAndEstado(
	            email, EstadoInvitacion.PENDIENTE);

	    for (Invitacion invitacion : invitaciones) {
	        invitacion.setUsuarioInvitado(usuario);
	        invitacion.setEstado(EstadoInvitacion.ACEPTADA);
	        invitacion.setFechaAceptacion(LocalDateTime.now());

	        invitacionRepository.save(invitacion);

	        if (invitacion.getViaje().getCreadoPor() != null
	                && invitacion.getViaje().getCreadoPor().getEmail() != null) {

	            emailService.notificarCreadorInvitacionAceptada(
	                    invitacion.getViaje().getCreadoPor().getEmail(),
	                    usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail(),
	                    invitacion.getViaje().getTitulo()
	            );

	            NotificacionDTO notificacion = new NotificacionDTO();
	            notificacion.setUsuarioId(invitacion.getViaje().getCreadoPor().getId());
	            notificacion.setEmailDestino(invitacion.getViaje().getCreadoPor().getEmail());
	            notificacion.setContenido("El usuario " +
	                    (usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail()) +
	                    " ha aceptado tu invitación al viaje '" +
	                    invitacion.getViaje().getTitulo() + "'.");

	            notificacionService.crearNotificacion(notificacion);
	        }
	    }
	}

}
