package com.packandgo.service;

import com.packandgo.dto.UsuarioDTO;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.AuthProvider;
import com.packandgo.enums.RolUsuario;
import com.packandgo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Override
    public List<UsuarioDTO> buscarClientes(String nombre, String email, AuthProvider provider, Date fechaCreacion) {
        Specification<Usuario> spec = Specification
                .where(hasRolCliente())
                .and(hasNombre(nombre))
                .and(hasEmail(email))
                .and(hasProvider(provider))
                .and(hasFechaCreacion(fechaCreacion));

        return usuarioRepository.findAll(spec).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRolUsuario(),
                usuario.getProvider(),
                usuario.isCuentaBloqueada(),
                usuario.getFechaCreacion(),
                usuario.getFechaBaja()
        );
    }

    @Override
    public void bloquearUsuario(Long id, boolean bloquear) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setCuentaBloqueada(bloquear);
        usuarioRepository.save(usuario);
    }

    private Specification<Usuario> hasNombre(String nombre) {
        return (root, query, builder) ->
                nombre == null ? null : builder.like(builder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    private Specification<Usuario> hasEmail(String email) {
        return (root, query, builder) ->
                email == null ? null : builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private Specification<Usuario> hasProvider(AuthProvider provider) {
        return (root, query, builder) ->
                provider == null ? null : builder.equal(root.get("provider"), provider);
    }

    private Specification<Usuario> hasFechaCreacion(Date fecha) {
        return (root, query, builder) ->
                fecha == null ? null : builder.greaterThanOrEqualTo(root.get("fechaCreacion"), fecha);
    }

    private Specification<Usuario> hasRolCliente() {
        return (root, query, builder) ->
                builder.equal(root.get("rolUsuario"), RolUsuario.CLIENTE);
    }
}
