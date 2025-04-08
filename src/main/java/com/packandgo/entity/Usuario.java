package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.packandgo.enums.AuthProvider;
import com.packandgo.enums.RolUsuario;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED) // Herencia de entidades
public class Usuario implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rolUsuario = RolUsuario.CLIENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL; // LOCAL por defecto

    @Column(name = "cuenta_bloqueada", nullable = false)
    private boolean cuentaBloqueada = false;

    @Column(name = "cuenta_expirada", nullable = false)
    private boolean cuentaExpirada = false;

    @Column(name = "password_expirado", nullable = false)
    private boolean passwordExpirado = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

    @Temporal(TemporalType.DATE)
    private Date fechaBaja; // Un usuario se considera inactivo si tiene fecha de baja

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = new Date();
    }

    public boolean isCuentaActiva() {
        return fechaBaja == null; // Un usuario activo es aquel que no tiene fecha de baja
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rolUsuario.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !cuentaExpirada;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !cuentaBloqueada;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !passwordExpirado;
    }

    @Override
    public boolean isEnabled() {
        return isCuentaActiva();
    }
    
    public Usuario() {
        this.provider = AuthProvider.LOCAL; // Valor por defecto
    }
}
