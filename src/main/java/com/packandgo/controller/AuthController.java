package com.packandgo.controller;

import com.packandgo.dto.SolicitudAutenticacion;
import com.packandgo.dto.SolicitudRegistro;
import com.packandgo.dto.SolicitudRecuperacionPassword;
import com.packandgo.dto.SolicitudResetPassword;
import com.packandgo.entity.Cliente;
import com.packandgo.entity.Empleado;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.AuthProvider;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.ClienteRepository;
import com.packandgo.repository.EmpleadoRepository;
import com.packandgo.repository.UsuarioRepository;
import com.packandgo.service.auth.AuthService;
import com.packandgo.service.EmailService;
import com.packandgo.service.InvitacionService;
import com.packandgo.utils.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UsuarioRepository usuarioRepository;
	private final ClienteRepository clienteRepository;
	private final EmpleadoRepository empleadoRepository;
	private final EmailService emailService;
	private final InvitacionService invitacionService;

	private static final Map<String, Pair<String, Long>> recoveryCodes = new ConcurrentHashMap<>();

	@PostMapping("/signup")
	public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SolicitudRegistro solicitudRegistro) {
		if (usuarioRepository.findByEmail(solicitudRegistro.getEmail()).isPresent()) {
			return new ResponseEntity<>("El email ya está en uso", HttpStatus.NOT_ACCEPTABLE);
		}

		RolUsuario rol;
		try {
			rol = RolUsuario.valueOf(solicitudRegistro.getRolUsuario().name());
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>("Rol no válido", HttpStatus.BAD_REQUEST);
		}

		Usuario usuario;

		if (rol == RolUsuario.CLIENTE) {
			Cliente cliente = new Cliente();
			cliente.setNombre(solicitudRegistro.getNombre());
			cliente.setEmail(solicitudRegistro.getEmail());
			cliente.setPassword(authService.encriptarPassword(solicitudRegistro.getPassword()));
			cliente.setRolUsuario(rol);
			cliente.setApellido1(solicitudRegistro.getApellido1());
			cliente.setApellido2(solicitudRegistro.getApellido2());
			cliente.setDni(solicitudRegistro.getDni());
			cliente.setTelefono(solicitudRegistro.getTelefono());
			cliente.setDomicilio(solicitudRegistro.getDomicilio());
			cliente.setFechaNacimiento(solicitudRegistro.getFechaNacimiento());
			cliente.setMetodoPago(solicitudRegistro.getMetodoPago());
			cliente.setNotificaciones(solicitudRegistro.getNotificaciones());
			cliente.setFechaCreacion(new Date());

			clienteRepository.save(cliente);
			usuario = cliente;

			invitacionService.aceptarInvitacionesPendientes(cliente.getEmail(), cliente);

		} else if (rol == RolUsuario.EMPLEADO) {
			Empleado empleado = new Empleado();
			empleado.setNombre(solicitudRegistro.getNombre());
			empleado.setEmail(solicitudRegistro.getEmail());
			empleado.setPassword(authService.encriptarPassword(solicitudRegistro.getPassword()));
			empleado.setRolUsuario(rol);
			empleado.setApellido1(solicitudRegistro.getApellido1());
			empleado.setApellido2(solicitudRegistro.getApellido2());
			empleado.setDni(solicitudRegistro.getDni());
			empleado.setTelefono(solicitudRegistro.getTelefono());
			empleado.setDomicilio(solicitudRegistro.getDomicilio());
			empleado.setSalario(solicitudRegistro.getSalario());
			empleado.setFechaContratacion(solicitudRegistro.getFechaContratacion());
			empleado.setFechaCese(solicitudRegistro.getFechaCese());
			empleado.setFechaCreacion(new Date());

			empleadoRepository.save(empleado);
			usuario = empleado;
		} else {
			return new ResponseEntity<>("Rol no válido", HttpStatus.BAD_REQUEST);
		}

		final String jwt = jwtUtil.generarToken(usuario);
		final String refreshToken = jwtUtil.generarRefreshToken(usuario);

		Map<String, Object> response = new HashMap<>();
		response.put("jwt", jwt);
		response.put("refreshToken", refreshToken);
		response.put("role", usuario.getRolUsuario().name());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<?> iniciarSesion(@RequestBody SolicitudAutenticacion solicitudAutenticacion) {
		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					solicitudAutenticacion.getEmail(), 
					solicitudAutenticacion.getPassword()
				)
			);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>("Correo o contraseña incorrectos", HttpStatus.BAD_REQUEST);
		} catch (DisabledException e) {
			return new ResponseEntity<>("Cuenta deshabilitada", HttpStatus.FORBIDDEN);
		}

		final Usuario userDetails = (Usuario) usuarioRepository.findByEmail(solicitudAutenticacion.getEmail()).orElse(null);
		if (userDetails == null) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}

		final String jwt = jwtUtil.generarToken(userDetails);
		final String refreshToken = jwtUtil.generarRefreshToken(userDetails);

		Map<String, Object> response = new HashMap<>();
		response.put("jwt", jwt);
		response.put("refreshToken", refreshToken);
		response.put("role", userDetails.getRolUsuario().name());
		response.put("cuentaBloqueada", userDetails.isCuentaBloqueada());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/google-login")
	public void redirectToGoogle(HttpServletResponse response) throws IOException {
		response.sendRedirect("/oauth2/authorization/google");
	}

	@GetMapping("/oauth2/success")
	public ResponseEntity<?> googleLoginSuccess(OAuth2AuthenticationToken authentication) {
		String email = authentication.getPrincipal().getAttribute("email");
		String name = authentication.getPrincipal().getAttribute("given_name");

		Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

		if (usuario == null) {
			Cliente cliente = new Cliente();
			cliente.setEmail(email);
			cliente.setNombre(name);
			cliente.setRolUsuario(RolUsuario.CLIENTE);
			cliente.setProvider(AuthProvider.GOOGLE);
			cliente.setFechaCreacion(new Date());

			clienteRepository.save(cliente);
			usuario = cliente;

			// Asociar invitaciones pendientes y enviar notificación al creador
			invitacionService.aceptarInvitacionesPendientes(cliente.getEmail(), cliente);
		}

		String token = jwtUtil.generarToken(usuario);

		return ResponseEntity.ok(Map.of("jwt", token, "email", email, "name", name));
	}

	@GetMapping("/oauth2/failure")
	public ResponseEntity<String> googleLoginFailure() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error en la autenticación con Google");
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
		String refreshToken = request.get("refreshToken");
		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token es necesario");
		}

		String username = jwtUtil.getUsernameFromToken(refreshToken);
		if (username == null || !jwtUtil.validateToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
		}

		final UserDetails userDetails = usuarioRepository.findByEmail(username).orElse(null);
		if (userDetails == null) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}

		final String newJwt = jwtUtil.generarToken(userDetails);

		Map<String, Object> response = new HashMap<>();
		response.put("jwt", newJwt);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/recover-password")
	public ResponseEntity<Map<String, String>> recoverPassword(@RequestBody SolicitudRecuperacionPassword request) {
		Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
		if (usuario == null) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "Correo no registrado"));
		}

		String codigo = String.valueOf(new Random().nextInt(900000) + 100000);
		recoveryCodes.put(request.getEmail(), Pair.of(codigo, System.currentTimeMillis()));

		emailService.sendEmail(request.getEmail(), "Código de recuperación", "Tu código es: " + codigo);

		Map<String, String> response = new HashMap<>();
		response.put("mensaje", "Código enviado a tu correo");
		response.put("codigo", codigo);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody SolicitudResetPassword request) {
		Pair<String, Long> storedData = recoveryCodes.get(request.getEmail());

		if (storedData == null || !storedData.getLeft().equals(request.getCodigo())) {
			return ResponseEntity.badRequest().body("Código incorrecto o expirado");
		}

		if (System.currentTimeMillis() - storedData.getRight() > 600000) {
			recoveryCodes.remove(request.getEmail());
			return ResponseEntity.badRequest().body("Código expirado");
		}

		Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
		if (usuario == null) {
			return ResponseEntity.badRequest().body("Usuario no encontrado");
		}

		usuario.setPassword(authService.encriptarPassword(request.getNuevaPassword()));
		usuarioRepository.save(usuario);

		recoveryCodes.remove(request.getEmail());

		return ResponseEntity.ok("Contraseña restablecida correctamente");
	}
}
