package com.packandgo.service.auth;

import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.dto.UsuarioDTO;
import com.packandgo.entity.Cliente;
import com.packandgo.entity.Empleado;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${ADMIN_EMAIL}")
	private String adminEmail;

	@Value("${ADMIN_PASSWORD}")
	private String adminPassword;

	@PostConstruct
	public void crearCuentaAdmin() {
		Optional<Usuario> cuentaAdmin = usuarioRepository.findByRolUsuario(RolUsuario.ADMIN);
		if (cuentaAdmin.isEmpty()) {
			Usuario nuevaCuentaAdmin = new Usuario();
			nuevaCuentaAdmin.setNombre("Admin");
			nuevaCuentaAdmin.setEmail(adminEmail);
			nuevaCuentaAdmin.setPassword(passwordEncoder.encode(adminPassword));
			nuevaCuentaAdmin.setRolUsuario(RolUsuario.ADMIN);
			usuarioRepository.save(nuevaCuentaAdmin);
			System.out.println("Cuenta de administrador creada");
		} else {
			System.out.println("La cuenta de administrador ya existe.");
		}
	}

	@Override
	public UsuarioDTO crearUsuario(SolicitudRegistro solicitudRegistro) {
		Usuario usuario;

		if (solicitudRegistro.getRolUsuario() == RolUsuario.CLIENTE) {
			Cliente cliente = new Cliente();
			cliente.setApellido1(solicitudRegistro.getApellido1());
			cliente.setApellido2(solicitudRegistro.getApellido2());
			cliente.setDni(solicitudRegistro.getDni());
			cliente.setTelefono(solicitudRegistro.getTelefono());
			cliente.setDomicilio(solicitudRegistro.getDomicilio());
			cliente.setFechaNacimiento(solicitudRegistro.getFechaNacimiento());
			cliente.setMetodoPago(solicitudRegistro.getMetodoPago());
			cliente.setNotificaciones(solicitudRegistro.getNotificaciones());
			usuario = cliente;
		} else if (solicitudRegistro.getRolUsuario() == RolUsuario.EMPLEADO) {
			Empleado empleado = new Empleado();
			empleado.setApellido1(solicitudRegistro.getApellido1());
			empleado.setApellido2(solicitudRegistro.getApellido2());
			empleado.setDni(solicitudRegistro.getDni());
			empleado.setTelefono(solicitudRegistro.getTelefono());
			empleado.setDomicilio(solicitudRegistro.getDomicilio());
			empleado.setSalario(solicitudRegistro.getSalario());
			empleado.setFechaContratacion(solicitudRegistro.getFechaContratacion());
			empleado.setFechaCese(solicitudRegistro.getFechaCese());
			usuario = empleado;
		} else {
			usuario = new Usuario();
		}

		usuario.setNombre(solicitudRegistro.getNombre());
		usuario.setEmail(solicitudRegistro.getEmail());
		usuario.setPassword(passwordEncoder.encode(solicitudRegistro.getPassword()));
		usuario.setRolUsuario(solicitudRegistro.getRolUsuario());

		Usuario usuarioCreado = usuarioRepository.save(usuario);

		return new UsuarioDTO(usuarioCreado.getId(), usuarioCreado.getNombre(), usuarioCreado.getEmail(),
				usuarioCreado.getRolUsuario(), usuarioCreado.getProvider(),usuarioCreado.isCuentaBloqueada(), usuarioCreado.getFechaCreacion(),
				usuarioCreado.getFechaBaja());
	}

	@Override
	public boolean existeUsuarioPorEmail(String email) {
		return usuarioRepository.findByEmail(email).isPresent();
	}

	@Override
	public String encriptarPassword(String password) {
		return passwordEncoder.encode(password);
	}
}
