package com.packandgo.service.auth;

import com.packandgo.entity.Cliente;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.AuthProvider;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.ClienteRepository;
import com.packandgo.repository.UsuarioRepository;
import com.packandgo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final JwtUtil jwtUtil;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nombre = oAuth2User.getAttribute("given_name");

        Optional<Usuario> existingUser = usuarioRepository.findByEmail(email);
        Usuario usuario;

        if (existingUser.isPresent()) {
            usuario = existingUser.get();
        } else {
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setEmail(email);
            cliente.setRolUsuario(RolUsuario.CLIENTE);
            cliente.setProvider(AuthProvider.GOOGLE);
            cliente.setFechaCreacion(new Date());
            usuario = clienteRepository.save(cliente);
        }

        String token = jwtUtil.generarToken(usuario);

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);

        response.addCookie(jwtCookie);

        // Redirigir al frontend utilizando variable de entorno
        response.sendRedirect(frontendUrl);
    }
}
