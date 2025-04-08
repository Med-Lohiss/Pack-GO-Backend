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
import com.packandgo.utils.JwtUtil;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    // 📌 Mapa temporal para almacenar los códigos de recuperación (email -> código y timestamp)
    private static final Map<String, Pair<String, Long>> recoveryCodes = new ConcurrentHashMap<>();

    /**
     * 📌 Registro de usuarios (Clientes o Empleados)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SolicitudRegistro solicitudRegistro) {
        // Verificar si el email ya está en uso
        if (usuarioRepository.findByEmail(solicitudRegistro.getEmail()).isPresent()) {
            return new ResponseEntity<>("El email ya está en uso", HttpStatus.NOT_ACCEPTABLE);
        }

        // Determinar el rol del usuario
        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(solicitudRegistro.getRolUsuario().name());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Rol no válido", HttpStatus.BAD_REQUEST);
        }

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
            return new ResponseEntity<>("Cliente registrado correctamente", HttpStatus.CREATED);
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
            return new ResponseEntity<>("Empleado registrado correctamente", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Rol no válido", HttpStatus.BAD_REQUEST);
    }

    /**
     * 📌 Inicio de sesión y generación de JWT
     */
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

        final UserDetails userDetails = usuarioRepository.findByEmail(solicitudAutenticacion.getEmail()).orElse(null);
        if (userDetails == null) {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }

        final String jwt = jwtUtil.generarToken(userDetails);
        final String refreshToken = jwtUtil.generarRefreshToken(userDetails); // También generamos un refresh token

        // Construir la respuesta con el token, el refresh token y el rol
        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwt);
        response.put("refreshToken", refreshToken);
        response.put("role", ((Usuario) userDetails).getRolUsuario().name());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/oauth2/success")
    public ResponseEntity<?> googleLoginSuccess(OAuth2AuthenticationToken authentication) {
        String email = authentication.getPrincipal().getAttribute("email");
        String name = authentication.getPrincipal().getAttribute("given_name");

        // Buscar si el usuario ya existe
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        
        if (usuario == null) {
            // Crear un nuevo cliente si no existe
            Cliente cliente = new Cliente();
            cliente.setEmail(email);
            cliente.setNombre(name);
            cliente.setRolUsuario(RolUsuario.CLIENTE);
            cliente.setProvider(AuthProvider.GOOGLE);  // Registrar que viene de Google
            cliente.setFechaCreacion(new Date());

            clienteRepository.save(cliente);
            usuario = cliente;
        }

        // Generar JWT
        String token = jwtUtil.generarToken(usuario);

        // Devolver el token al frontend
        return ResponseEntity.ok(Map.of("jwt", token, "email", email, "name", name));
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> googleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error en la autenticación con Google");
    }


    /**
     * 📌 Endpoint para obtener un nuevo JWT usando el refresh token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token es necesario");
        }

        // Verificar si el refresh token es válido
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        if (username == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
        }

        // Generar un nuevo JWT para el usuario
        final UserDetails userDetails = usuarioRepository.findByEmail(username).orElse(null);
        if (userDetails == null) {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }

        final String newJwt = jwtUtil.generarToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", newJwt);

        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Recuperación de contraseña - Enviar código al correo
     */
    @PostMapping("/recover-password")
    public ResponseEntity<Map<String, String>> recoverPassword(@RequestBody SolicitudRecuperacionPassword request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Correo no registrado"));
        }

        // Generar un código aleatorio de 6 dígitos
        String codigo = String.valueOf(new Random().nextInt(900000) + 100000);
        // Guardar el código junto con la fecha de creación
        recoveryCodes.put(request.getEmail(), Pair.of(codigo, System.currentTimeMillis()));

        // Enviar código por email
        emailService.sendEmail(request.getEmail(), "Código de recuperación", "Tu código es: " + codigo);

        System.out.println("Código almacenado: " + recoveryCodes.get(request.getEmail()));

        // Devolver el código en la respuesta
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Código enviado a tu correo");
        response.put("codigo", codigo);  // Devolvemos el código para que el frontend lo almacene

        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Restablecer la contraseña
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody SolicitudResetPassword request) {
        // Recuperar el código de recuperación y la fecha de creación almacenados para este correo
        Pair<String, Long> storedData = recoveryCodes.get(request.getEmail());

        // Verificar si el código existe y si coincide
        if (storedData == null || !storedData.getLeft().equals(request.getCodigo())) {
            return ResponseEntity.badRequest().body("Código incorrecto o expirado");
        }

        // Expirar el código después de 10 minutos (600,000 ms)
        if (System.currentTimeMillis() - storedData.getRight() > 600000) {  // 10 minutos
            recoveryCodes.remove(request.getEmail());
            return ResponseEntity.badRequest().body("Código expirado");
        }

        // Buscar el usuario por el correo electrónico
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        // Encriptar y actualizar la contraseña
        usuario.setPassword(authService.encriptarPassword(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        // Eliminar el código usado para evitar que se vuelva a utilizar
        recoveryCodes.remove(request.getEmail());

        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }
}
