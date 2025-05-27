package com.packandgo.controller;

import com.packandgo.dto.ViajeDTO;
import com.packandgo.dto.ActividadDTO;
import com.packandgo.dto.ComentarioDTO;
import com.packandgo.dto.ReporteDTO;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.RolUsuario;
import com.packandgo.service.ViajeService;
import com.packandgo.service.PexelsService;
import com.packandgo.service.ActividadService;
import com.packandgo.service.ComentarioService;
import com.packandgo.service.ReporteService;
import com.packandgo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/empleado")
@RequiredArgsConstructor
public class EmpleadoController {

	private final ViajeService viajeService;
	private final PexelsService pexelsService;
	private final ActividadService actividadService;
	private final ComentarioService comentarioService;
	private final ReporteService reporteService;
	private final UsuarioRepository usuarioRepository;

	//Viajes
	@GetMapping("/viajes")
	public ResponseEntity<Map<String, List<ViajeDTO>>> listarViajesPorRol() {
		List<ViajeDTO> viajes = viajeService.listarTodosLosViajes();

		List<ViajeDTO> viajesDeEmpleado = new ArrayList<>();
		List<ViajeDTO> viajesDeCliente = new ArrayList<>();

		for (ViajeDTO viaje : viajes) {
			Usuario creador = viajeService.obtenerUsuarioPorId(viaje.getCreadoPorId());

			if (RolUsuario.EMPLEADO.equals(creador.getRolUsuario())) {
				viajesDeEmpleado.add(viaje);
			} else if (RolUsuario.CLIENTE.equals(creador.getRolUsuario())) {
				viajesDeCliente.add(viaje);
			}
		}

		Map<String, List<ViajeDTO>> viajesPorRol = new HashMap<>();
		viajesPorRol.put("empleados", viajesDeEmpleado);
		viajesPorRol.put("clientes", viajesDeCliente);

		return ResponseEntity.ok(viajesPorRol);
	}

	@PostMapping("/viajes")
	public ResponseEntity<ViajeDTO> crearViaje(@RequestBody ViajeDTO viajeDTO, Principal principal) {
		String emailEmpleado = principal.getName();
		ViajeDTO creado = viajeService.crearViajeComoEmpleado(emailEmpleado, viajeDTO);
		return ResponseEntity.ok(creado);
	}

	@GetMapping("/viajes/{viajeId}")
	public ResponseEntity<ViajeDTO> obtenerViajePorId(@PathVariable Long viajeId) {
		ViajeDTO viaje = viajeService.obtenerViajePorIdComoEmpleado(viajeId);
		return ResponseEntity.ok(viaje);
	}

	@PutMapping("/viajes/{viajeId}")
	public ResponseEntity<ViajeDTO> editarViaje(@PathVariable Long viajeId, @RequestBody ViajeDTO viajeDTO,
			Principal principal) {
		String emailEmpleado = principal.getName();
		ViajeDTO actualizado = viajeService.editarViajeComoEmpleado(emailEmpleado, viajeId, viajeDTO);
		return ResponseEntity.ok(actualizado);
	}

	@PatchMapping("/viajes/{viajeId}/compartir")
	public ResponseEntity<?> cambiarEstadoCompartido(@PathVariable Long viajeId,
			@RequestBody Map<String, Boolean> body, Principal principal) {
		boolean nuevoEstado = body.getOrDefault("compartido", false);
		String emailEmpleado = principal.getName();

		ViajeDTO viaje = viajeService.obtenerViajePorIdComoEmpleado(viajeId);
		viaje.setCompartido(nuevoEstado);
		viajeService.editarViajeComoEmpleado(emailEmpleado, viajeId, viaje);

		return ResponseEntity.ok("Estado de compartido actualizado correctamente.");
	}

	@DeleteMapping("/viajes/{viajeId}")
	public ResponseEntity<Void> eliminarViaje(@PathVariable Long viajeId) {
		viajeService.eliminarViajeComoEmpleado(viajeId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/pexels-imagen")
	public ResponseEntity<Map<String, String>> obtenerImagenDePexels(@RequestParam String ubicacion) {
		try {
			String imagenUrl = pexelsService.obtenerImagenPorUbicacion(ubicacion);
			if (imagenUrl != null) {
				Map<String, String> response = new HashMap<>();
				response.put("url", imagenUrl);
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}
	
	//Actividades
	@PostMapping("/viajes/{viajeId}/actividades")
	public ResponseEntity<ActividadDTO> crearActividad(@PathVariable Long viajeId,
			@RequestBody ActividadDTO actividadDTO) {
		actividadDTO.setViajeId(viajeId);
		ActividadDTO creada = actividadService.crearActividad(actividadDTO);
		return ResponseEntity.ok(creada);
	}

	@GetMapping("/viajes/{viajeId}/actividades")
	public ResponseEntity<List<ActividadDTO>> listarActividadesPorViaje(@PathVariable Long viajeId) {
		List<ActividadDTO> actividades = actividadService.obtenerActividadesPorViaje(viajeId);
		return ResponseEntity.ok(actividades);
	}

	@GetMapping("/actividades/{actividadId}")
	public ResponseEntity<ActividadDTO> obtenerActividad(@PathVariable Long actividadId) {
		ActividadDTO actividad = actividadService.obtenerActividadPorId(actividadId);
		return ResponseEntity.ok(actividad);
	}

	@PutMapping("/actividades/{actividadId}")
	public ResponseEntity<ActividadDTO> editarActividad(@PathVariable Long actividadId,
			@RequestBody ActividadDTO actividadDTO) {
		ActividadDTO actualizada = actividadService.actualizarActividad(actividadId, actividadDTO);
		return ResponseEntity.ok(actualizada);
	}

	@DeleteMapping("/actividades/{actividadId}")
	public ResponseEntity<Void> eliminarActividad(@PathVariable Long actividadId) {
		actividadService.eliminarActividad(actividadId);
		return ResponseEntity.noContent().build();
	}

	//Comentarios
	@PostMapping("/comentarios")
	public ResponseEntity<ComentarioDTO> crearComentario(@RequestBody Map<String, String> body,
	                                                     Principal principal) {
	    String contenido = body.get("contenido");
	    String emailAutor = principal.getName();
	    if (contenido == null || contenido.trim().isEmpty()) {
	        return ResponseEntity.badRequest().build();
	    }
	    ComentarioDTO creado = comentarioService.crearComentario(emailAutor, contenido);
	    return ResponseEntity.ok(creado);
	}

	@PutMapping("/comentarios/{comentarioId}")
	public ResponseEntity<ComentarioDTO> editarComentario(@PathVariable Long comentarioId,
	                                                      @RequestBody Map<String, String> body,
	                                                      Principal principal) {
	    String nuevoContenido = body.get("contenido");
	    String emailAutor = principal.getName();
	    ComentarioDTO actualizado = comentarioService.editarComentario(emailAutor, comentarioId, nuevoContenido);
	    return ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/comentarios/{comentarioId}")
	public ResponseEntity<Void> eliminarComentario(@PathVariable Long comentarioId,
	                                               Principal principal) {
	    String emailSolicitante = principal.getName();
	    comentarioService.eliminarComentario(emailSolicitante, comentarioId);
	    return ResponseEntity.noContent().build();
	}

	@GetMapping("/comentarios")
	public ResponseEntity<List<ComentarioDTO>> listarTodosComentarios() {
	    List<ComentarioDTO> comentarios = comentarioService.obtenerTodosComentarios();
	    return ResponseEntity.ok(comentarios);
	}

	@PatchMapping("/comentarios/{comentarioId}/aprobar")
	public ResponseEntity<String> aprobarComentario(@PathVariable Long comentarioId) {
		comentarioService.aprobarComentario(comentarioId);
		return ResponseEntity.ok("Comentario aprobado correctamente.");
	}
	
	//Reportes
	@PostMapping("/reportes")
	public ResponseEntity<ReporteDTO> crearReporte(@RequestBody ReporteDTO dto, Principal principal) {
		String email = principal.getName();
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		dto.setUsuarioReportanteId(usuario.getId());
		ReporteDTO creado = reporteService.crearReporte(dto);
		return ResponseEntity.ok(creado);
	}
}
