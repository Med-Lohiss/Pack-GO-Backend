package com.packandgo.controller;

import com.packandgo.dto.ClienteDTO;
import com.packandgo.dto.ViajeDTO;
import com.packandgo.service.ClienteService;
import com.packandgo.service.PexelsService;
import com.packandgo.service.ViajeService;
import com.packandgo.dto.ActividadDTO;
import com.packandgo.service.ActividadService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClienteController {

	private final ClienteService clienteService;
	private final ViajeService viajeService;
	private final PexelsService pexelsService;
	private final ActividadService actividadService;

	@GetMapping("/perfil")
	public ResponseEntity<ClienteDTO> getPerfil(@AuthenticationPrincipal UserDetails userDetails) {
		String email = userDetails.getUsername();
		ClienteDTO cliente = clienteService.obtenerPerfilCliente(email);
		return ResponseEntity.ok(cliente);
	}

	@PutMapping("/perfil")
	public ResponseEntity<ClienteDTO> actualizarPerfil(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody ClienteDTO datosActualizados) {
		String email = userDetails.getUsername();
		ClienteDTO actualizado = clienteService.actualizarPerfilCliente(email, datosActualizados);
		return ResponseEntity.ok(actualizado);
	}

	@PostMapping("/viajes")
	public ResponseEntity<ViajeDTO> crearViaje(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody ViajeDTO viajeDTO) {
		String email = userDetails.getUsername();
		ViajeDTO creado = viajeService.crearViaje(email, viajeDTO);
		return ResponseEntity.ok(creado);
	}

	@GetMapping("/viajes")
	public ResponseEntity<List<ViajeDTO>> listarViajes(@AuthenticationPrincipal UserDetails userDetails) {
		String email = userDetails.getUsername();
		List<ViajeDTO> viajes = viajeService.listarViajesUsuario(email);
		return ResponseEntity.ok(viajes);
	}

	@GetMapping("/viajes/{viajeId}")
	public ResponseEntity<ViajeDTO> obtenerViajePorId(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long viajeId) {
		String email = userDetails.getUsername();
		ViajeDTO viaje = viajeService.obtenerViajePorId(email, viajeId);
		return ResponseEntity.ok(viaje);
	}

	@PutMapping("/viajes/{viajeId}")
	public ResponseEntity<ViajeDTO> editarViaje(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long viajeId, @RequestBody ViajeDTO viajeDTO) {
		String email = userDetails.getUsername();
		ViajeDTO actualizado = viajeService.editarViaje(email, viajeId, viajeDTO);
		return ResponseEntity.ok(actualizado);
	}

	@DeleteMapping("/viajes/{viajeId}")
	public ResponseEntity<Void> eliminarViaje(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long viajeId) {
		String email = userDetails.getUsername();
		viajeService.eliminarViaje(email, viajeId);
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
