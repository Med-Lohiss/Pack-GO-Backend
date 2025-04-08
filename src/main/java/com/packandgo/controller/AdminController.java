package com.packandgo.controller;

import com.packandgo.dto.EmpleadoDTO;
import com.packandgo.dto.FiltroEmpleadoDTO;
import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin") // Nueva ruta base
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Endpoint para crear un empleado
    @PostMapping("/empleados")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> crearEmpleado(@RequestBody SolicitudRegistro solicitudRegistro) {
        // Verificar si el email ya está en uso
        if (adminService.buscarUsuarioPorEmail(solicitudRegistro.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("El email ya está en uso.");
        }

        EmpleadoDTO nuevoEmpleado = adminService.crearEmpleado(solicitudRegistro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    // Endpoint para obtener la lista de empleados
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/empleados") // Ruta relativa
    public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
        return ResponseEntity.ok(adminService.listarEmpleados());
    }

    // Endpoint para obtener un empleado por ID
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/empleados/{id}") // Ruta relativa
    public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Long id) {
        return adminService.obtenerEmpleadoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para actualizar un empleado por ID
    @PutMapping("/empleados/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody SolicitudRegistro solicitudRegistro) {
        return ResponseEntity.ok(adminService.actualizarEmpleado(id, solicitudRegistro));
    }

    // Endpoint para eliminar un empleado por ID
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/empleados/{id}") // Ruta relativa
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        adminService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Endpoint para filtrar empleados
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/empleados/filtrar") // Ruta relativa
    public ResponseEntity<List<EmpleadoDTO>> filtrarEmpleados(@RequestBody FiltroEmpleadoDTO filtro) {
        return ResponseEntity.ok(adminService.filtrarEmpleados(filtro));
    }
}
