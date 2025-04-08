package com.packandgo.repository;

import com.packandgo.entity.Usuario;
import com.packandgo.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByRolUsuario(RolUsuario rolUsuario);
}

