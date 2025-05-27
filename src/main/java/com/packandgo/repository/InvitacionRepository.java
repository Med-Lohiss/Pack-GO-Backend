package com.packandgo.repository;

import com.packandgo.entity.Invitacion;
import com.packandgo.entity.Usuario;
import com.packandgo.enums.EstadoInvitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
    Optional<Invitacion> findByToken(String token);

    List<Invitacion> findAllByUsuarioInvitadoAndEstado(Usuario usuario, EstadoInvitacion estado);
    
    List<Invitacion> findAllByEmailInvitadoAndEstado(String emailInvitado, EstadoInvitacion estado);
    
    boolean existsByViajeIdAndEmailInvitadoAndEstadoIn(Long viajeId, String emailInvitado, List<EstadoInvitacion> estados);
}
