package com.packandgo.service;

import com.packandgo.dto.EmpleadoDTO;
import com.packandgo.dto.FiltroEmpleadoDTO;
import com.packandgo.dto.SolicitudRegistro;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    EmpleadoDTO crearEmpleado(SolicitudRegistro solicitudRegistro);
    List<EmpleadoDTO> listarEmpleados();
    Optional<EmpleadoDTO> obtenerEmpleadoPorId(Long id);
    EmpleadoDTO actualizarEmpleado(Long id, SolicitudRegistro solicitudRegistro);
    void eliminarEmpleado(Long id);
    List<EmpleadoDTO> filtrarEmpleados(FiltroEmpleadoDTO filtro);
    Optional<EmpleadoDTO> buscarUsuarioPorEmail(String email);
}
