package com.packandgo.service;

import com.packandgo.dto.EmpleadoDTO;
import com.packandgo.dto.FiltroEmpleadoDTO;
import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.entity.Empleado;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.EmpleadoRepository;
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
    private final PasswordEncoder passwordEncoder; // Se inyecta el PasswordEncoder

    // Método para convertir SolicitudRegistro (DTO) a Empleado (Entidad)
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

        // Asignamos el rol de usuario (se puede cambiar según el rol en el DTO)
        empleado.setRolUsuario(solicitudRegistro.getRolUsuario());

        return empleado;
    }

    // Método para convertir Empleado a EmpleadoDTO
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
        // Encriptar la contraseña antes de guardar
        String contrasenaEncriptada = passwordEncoder.encode(solicitudRegistro.getPassword());
        
        // Setear la contraseña encriptada en el empleado
        empleado.setPassword(contrasenaEncriptada);

        // Asignar el rol EMPLEADO si no se ha asignado
        if (empleado.getRolUsuario() == null) {
            empleado.setRolUsuario(RolUsuario.EMPLEADO);
        }

        // Guardar el empleado en el repositorio
        Empleado saved = empleadoRepository.save(empleado);
        return toDTO(saved);
    }

    @Override
    public List<EmpleadoDTO> listarEmpleados() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EmpleadoDTO> obtenerEmpleadoPorId(Long id) {
        return empleadoRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional
    public EmpleadoDTO actualizarEmpleado(Long id, SolicitudRegistro solicitudRegistro) {
        Empleado empleadoExistente = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        // Actualizamos los campos del empleado con la información del DTO
        empleadoExistente.setNombre(solicitudRegistro.getNombre());
        empleadoExistente.setEmail(solicitudRegistro.getEmail());
        empleadoExistente.setDni(solicitudRegistro.getDni());
        empleadoExistente.setApellido1(solicitudRegistro.getApellido1());
        empleadoExistente.setApellido2(solicitudRegistro.getApellido2());
        empleadoExistente.setTelefono(solicitudRegistro.getTelefono());
        empleadoExistente.setDomicilio(solicitudRegistro.getDomicilio());
        empleadoExistente.setSalario(solicitudRegistro.getSalario());
        empleadoExistente.setFechaContratacion(solicitudRegistro.getFechaContratacion());
        empleadoExistente.setFechaCese(solicitudRegistro.getFechaCese());

        // Guardamos el empleado actualizado
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
            stream = stream.filter(e -> e.getNombre() != null && e.getNombre().toLowerCase().contains(filtro.getNombre().toLowerCase()));
        }
        if (filtro.getApellido1() != null) {
            stream = stream.filter(e -> e.getApellido1() != null && e.getApellido1().toLowerCase().contains(filtro.getApellido1().toLowerCase()));
        }
        if (filtro.getApellido2() != null) {
            stream = stream.filter(e -> e.getApellido2() != null && e.getApellido2().toLowerCase().contains(filtro.getApellido2().toLowerCase()));
        }
        if (filtro.getDni() != null) {
            stream = stream.filter(e -> e.getDni() != null && e.getDni().toLowerCase().contains(filtro.getDni().toLowerCase()));
        }
        if (filtro.getEmail() != null) {
            stream = stream.filter(e -> e.getEmail() != null && e.getEmail().toLowerCase().contains(filtro.getEmail().toLowerCase()));
        }

        // Ordenamiento simple
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

    // Implementación del método buscarUsuarioPorEmail
    @Override
    public Optional<EmpleadoDTO> buscarUsuarioPorEmail(String email) {
        return empleadoRepository.findByEmail(email).map(this::toDTO);
    }
}
