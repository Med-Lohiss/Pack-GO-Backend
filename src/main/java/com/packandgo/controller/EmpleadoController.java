package com.packandgo.controller;

import com.packandgo.dto.ViajeDTO;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.RolUsuario;
import com.packandgo.dto.ActividadDTO;
import com.packandgo.service.ViajeService;
import com.packandgo.service.PexelsService;
import com.packandgo.service.ActividadService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleado")
@RequiredArgsConstructor
public class EmpleadoController {

	private final ViajeService viajeService;
	private final PexelsService pexelsService;
	private final ActividadService actividadService;

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
	public ResponseEntity<?> cambiarEstadoCompartido(@PathVariable Long viajeId, @RequestBody Map<String, Boolean> body,
			Principal principal) {
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
}
