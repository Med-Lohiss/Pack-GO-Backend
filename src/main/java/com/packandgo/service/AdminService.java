package com.packandgo.service;

import com.packandgo.dto.EmpleadoDTO;
import com.packandgo.dto.EmpresaDTO;
import com.packandgo.dto.FiltroEmpleadoDTO;
import com.packandgo.dto.SolicitudRegistro;

import java.util.List;
import java.util.Optional;

public interface AdminService {

	EmpleadoDTO crearEmpleado(SolicitudRegistro solicitudRegistro);

	List<EmpleadoDTO> listarEmpleados();

	Optional<EmpleadoDTO> obtenerEmpleadoPorId(Long id);

	EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO empleadoDTO);

	void eliminarEmpleado(Long id);

	List<EmpleadoDTO> filtrarEmpleados(FiltroEmpleadoDTO filtro);

	Optional<EmpleadoDTO> buscarUsuarioPorEmail(String email);

	EmpresaDTO crearEmpresa(EmpresaDTO empresaDTO);

	Optional<EmpresaDTO> obtenerEmpresa();

	EmpresaDTO actualizarEmpresa(EmpresaDTO empresaDTO);
}
