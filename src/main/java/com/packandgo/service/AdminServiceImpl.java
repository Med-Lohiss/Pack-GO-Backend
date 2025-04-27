package com.packandgo.service;

import com.packandgo.dto.EmpleadoDTO;
import com.packandgo.dto.EmpresaDTO;
import com.packandgo.dto.FiltroEmpleadoDTO;
import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.entity.Empleado;
import com.packandgo.entity.Empresa;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.EmpleadoRepository;
import com.packandgo.repository.EmpresaRepository;

import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar la contraseña
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final EmpleadoRepository empleadoRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmpresaRepository empresaRepository;

	private Empleado toEntity(SolicitudRegistro solicitudRegistro) {
		Empleado empleado = new Empleado();
		empleado.setNombre(solicitudRegistro.getNombre());
		empleado.setEmail(solicitudRegistro.getEmail());
		empleado.setDni(solicitudRegistro.getDni());
		empleado.setApellido1(solicitudRegistro.getApellido1());
		empleado.setApellido2(solicitudRegistro.getApellido2());
		empleado.setTelefono(solicitudRegistro.getTelefono());
		empleado.setDomicilio(solicitudRegistro.getDomicilio());
		empleado.setSalario(solicitudRegistro.getSalario());
		empleado.setFechaContratacion(solicitudRegistro.getFechaContratacion());
		empleado.setFechaCese(solicitudRegistro.getFechaCese());

		empleado.setRolUsuario(solicitudRegistro.getRolUsuario());

		return empleado;
	}

	private EmpleadoDTO toDTO(Empleado empleado) {
		EmpleadoDTO dto = new EmpleadoDTO();
		dto.setId(empleado.getId());
		dto.setNombre(empleado.getNombre());
		dto.setEmail(empleado.getEmail());
		dto.setRolUsuario(empleado.getRolUsuario());
		dto.setProvider(empleado.getProvider());
		dto.setFechaCreacion(empleado.getFechaCreacion());
		dto.setFechaBaja(empleado.getFechaBaja());
		dto.setDni(empleado.getDni());
		dto.setApellido1(empleado.getApellido1());
		dto.setApellido2(empleado.getApellido2());
		dto.setTelefono(empleado.getTelefono());
		dto.setDomicilio(empleado.getDomicilio());
		dto.setSalario(empleado.getSalario());
		dto.setFechaContratacion(empleado.getFechaContratacion());
		dto.setFechaCese(empleado.getFechaCese());
		return dto;
	}

	@Override
	public EmpleadoDTO crearEmpleado(SolicitudRegistro solicitudRegistro) {
		Empleado empleado = toEntity(solicitudRegistro);

		String contrasenaEncriptada = passwordEncoder.encode(solicitudRegistro.getPassword());

		empleado.setPassword(contrasenaEncriptada);

		if (empleado.getRolUsuario() == null) {
			empleado.setRolUsuario(RolUsuario.EMPLEADO);
		}

		Empleado saved = empleadoRepository.save(empleado);
		return toDTO(saved);
	}

	@Override
	public List<EmpleadoDTO> listarEmpleados() {
		return empleadoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public Optional<EmpleadoDTO> obtenerEmpleadoPorId(Long id) {
		return empleadoRepository.findById(id).map(this::toDTO);
	}

	@Override
	@Transactional
	public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO empleadoDTO) {
		Empleado empleadoExistente = empleadoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

		empleadoExistente.setNombre(empleadoDTO.getNombre());
		empleadoExistente.setEmail(empleadoDTO.getEmail());
		empleadoExistente.setDni(empleadoDTO.getDni());
		empleadoExistente.setApellido1(empleadoDTO.getApellido1());
		empleadoExistente.setApellido2(empleadoDTO.getApellido2());
		empleadoExistente.setTelefono(empleadoDTO.getTelefono());
		empleadoExistente.setDomicilio(empleadoDTO.getDomicilio());
		empleadoExistente.setSalario(empleadoDTO.getSalario());
		empleadoExistente.setFechaContratacion(empleadoDTO.getFechaContratacion());
		empleadoExistente.setFechaCese(empleadoDTO.getFechaCese());

		if (empleadoDTO.getRolUsuario() != null) {
			empleadoExistente.setRolUsuario(empleadoDTO.getRolUsuario());
		}

		System.out.println("Empleado actualizado: " + empleadoExistente);

		empleadoRepository.saveAndFlush(empleadoExistente);
		return toDTO(empleadoExistente);
	}

	@Override
	public void eliminarEmpleado(Long id) {
		if (!empleadoRepository.existsById(id)) {
			throw new RuntimeException("Empleado no encontrado");
		}
		empleadoRepository.deleteById(id);
	}

	@Override
	public List<EmpleadoDTO> filtrarEmpleados(FiltroEmpleadoDTO filtro) {
		List<Empleado> empleados = empleadoRepository.findAll();

		Stream<Empleado> stream = empleados.stream();

		if (filtro.getNombre() != null) {
			stream = stream.filter(e -> e.getNombre() != null
					&& e.getNombre().toLowerCase().contains(filtro.getNombre().toLowerCase()));
		}
		if (filtro.getApellido1() != null) {
			stream = stream.filter(e -> e.getApellido1() != null
					&& e.getApellido1().toLowerCase().contains(filtro.getApellido1().toLowerCase()));
		}
		if (filtro.getApellido2() != null) {
			stream = stream.filter(e -> e.getApellido2() != null
					&& e.getApellido2().toLowerCase().contains(filtro.getApellido2().toLowerCase()));
		}
		if (filtro.getDni() != null) {
			stream = stream.filter(
					e -> e.getDni() != null && e.getDni().toLowerCase().contains(filtro.getDni().toLowerCase()));
		}
		if (filtro.getEmail() != null) {
			stream = stream.filter(
					e -> e.getEmail() != null && e.getEmail().toLowerCase().contains(filtro.getEmail().toLowerCase()));
		}

		Comparator<Empleado> comparator = null;
		if ("salario".equalsIgnoreCase(filtro.getOrdenarPor())) {
			comparator = Comparator.comparing(Empleado::getSalario, Comparator.nullsLast(Double::compareTo));
		} else if ("fechaContratacion".equalsIgnoreCase(filtro.getOrdenarPor())) {
			comparator = Comparator.comparing(Empleado::getFechaContratacion, Comparator.nullsLast(Date::compareTo));
		}

		if (comparator != null) {
			if ("desc".equalsIgnoreCase(filtro.getOrden())) {
				comparator = comparator.reversed();
			}
			stream = stream.sorted(comparator);
		}

		return stream.map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public Optional<EmpleadoDTO> buscarUsuarioPorEmail(String email) {
		return empleadoRepository.findByEmail(email).map(this::toDTO);
	}

	private Empresa toEntity(EmpresaDTO dto) {
		return Empresa.builder().id(dto.getId()).cif(dto.getCif()).denominacionSocial(dto.getDenominacionSocial())
				.domicilio(dto.getDomicilio()).fechaConstitucion(dto.getFechaConstitucion())
				.direccionWeb(dto.getDireccionWeb()).telefono(dto.getTelefono()).emailContacto(dto.getEmailContacto())
				.build();
	}

	private EmpresaDTO toDTO(Empresa empresa) {
		return EmpresaDTO.builder().id(empresa.getId()).cif(empresa.getCif())
				.denominacionSocial(empresa.getDenominacionSocial()).domicilio(empresa.getDomicilio())
				.fechaConstitucion(empresa.getFechaConstitucion()).direccionWeb(empresa.getDireccionWeb())
				.telefono(empresa.getTelefono()).emailContacto(empresa.getEmailContacto()).build();
	}

	@Override
	public EmpresaDTO crearEmpresa(EmpresaDTO empresaDTO) {

		if (empresaDTO.getId() != null) {
			throw new IllegalArgumentException("El ID no debe estar presente al crear una nueva empresa.");
		}

		Empresa empresa = toEntity(empresaDTO);

		empresa = empresaRepository.save(empresa);

		return toDTO(empresa);
	}

	@Override
	public Optional<EmpresaDTO> obtenerEmpresa() {
		return empresaRepository.findAll().stream().findFirst().map(this::toDTO);
	}

	@Override
	@Transactional
	public EmpresaDTO actualizarEmpresa(EmpresaDTO empresaDTO) {

		if (empresaDTO.getId() == null) {
			throw new IllegalArgumentException("El ID es necesario para actualizar la empresa.");
		}

		Empresa empresaExistente = empresaRepository.findById(empresaDTO.getId())
				.orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaDTO.getId()));

		empresaExistente.setCif(empresaDTO.getCif());
		empresaExistente.setDenominacionSocial(empresaDTO.getDenominacionSocial());
		empresaExistente.setDomicilio(empresaDTO.getDomicilio());
		empresaExistente.setFechaConstitucion(empresaDTO.getFechaConstitucion());
		empresaExistente.setDireccionWeb(empresaDTO.getDireccionWeb());
		empresaExistente.setTelefono(empresaDTO.getTelefono());
		empresaExistente.setEmailContacto(empresaDTO.getEmailContacto());

		return toDTO(empresaExistente);
	}

}
