package com.packandgo.controller;

import com.packandgo.dto.EmpleadoDTO;
import com.packandgo.dto.EmpresaDTO;
import com.packandgo.dto.FiltroEmpleadoDTO;
import com.packandgo.dto.ReporteDTO;
import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.dto.UsuarioDTO;
import com.packandgo.enums.AuthProvider;
import com.packandgo.service.AdminService;
import com.packandgo.service.ReporteService;
import com.packandgo.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;
	private final ReporteService reporteService;
	private final UsuarioService usuarioService;

	// Empleados

	@PostMapping("/empleados")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<?> crearEmpleado(@RequestBody SolicitudRegistro solicitudRegistro) {
		if (adminService.buscarUsuarioPorEmail(solicitudRegistro.getEmail()).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("El email ya está en uso.");
		}

		EmpleadoDTO nuevoEmpleado = adminService.crearEmpleado(solicitudRegistro);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/empleados")
	public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
		return ResponseEntity.ok(adminService.listarEmpleados());
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/empleados/{id}")
	public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Long id) {
		return adminService.obtenerEmpleadoPorId(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/empleados/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<?> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO empleadoDTO) {
		Optional<EmpleadoDTO> existente = adminService.buscarUsuarioPorEmail(empleadoDTO.getEmail());
		if (existente.isPresent() && !existente.get().getId().equals(id)) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("error", "El email ya está en uso."));
		}

		EmpleadoDTO actualizado = adminService.actualizarEmpleado(id, empleadoDTO);
		return ResponseEntity.ok(actualizado);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/empleados/{id}")
	public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
		adminService.eliminarEmpleado(id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/empleados/filtrar")
	public ResponseEntity<List<EmpleadoDTO>> filtrarEmpleados(@RequestBody FiltroEmpleadoDTO filtro) {
		return ResponseEntity.ok(adminService.filtrarEmpleados(filtro));
	}

	// Empresa

	@PostMapping("/empresa")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<EmpresaDTO> crearEmpresa(@RequestBody EmpresaDTO empresaDTO) {
		if (empresaDTO.getId() != null) {
			return ResponseEntity.badRequest().body(null);
		}

		EmpresaDTO nuevaEmpresa = adminService.crearEmpresa(empresaDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
	}

	@GetMapping("/empresa")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<EmpresaDTO> obtenerEmpresa() {
		return adminService.obtenerEmpresa()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}

	@PutMapping("/empresa")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<EmpresaDTO> actualizarEmpresa(@RequestBody EmpresaDTO empresaDTO) {
		EmpresaDTO empresaActualizada = adminService.actualizarEmpresa(empresaDTO);
		return ResponseEntity.ok(empresaActualizada);
	}

	// Reportes

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/reportes")
	public ResponseEntity<List<ReporteDTO>> listarReportes() {
		List<ReporteDTO> reportes = reporteService.listarReportes();
		return ResponseEntity.ok(reportes);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/reportes/{id}")
	public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
		reporteService.eliminarReporte(id);
		return ResponseEntity.noContent().build();
	}

	// Clientes

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/clientes")
	public ResponseEntity<List<UsuarioDTO>> listarClientes() {
		List<UsuarioDTO> clientes = usuarioService.buscarClientes(null, null, null, null);
		return ResponseEntity.ok(clientes);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/clientes/filtrar")
	public ResponseEntity<List<UsuarioDTO>> filtrarClientes(
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) AuthProvider provider,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaCreacion
	) {
		List<UsuarioDTO> filtrados = usuarioService.buscarClientes(nombre, email, provider, fechaCreacion);
		return ResponseEntity.ok(filtrados);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/clientes/{id}/bloquear")
	public ResponseEntity<Void> bloquearUsuario(@PathVariable Long id, @RequestParam boolean bloquear) {
		usuarioService.bloquearUsuario(id, bloquear);
		return ResponseEntity.noContent().build();
	}
}
