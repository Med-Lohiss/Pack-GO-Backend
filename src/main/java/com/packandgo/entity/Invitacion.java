package com.packandgo.entity;

import com.packandgo.enums.EstadoInvitacion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invitaciones")
public class Invitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailInvitado;

    private String token;

    @Enumerated(EnumType.STRING)
    private EstadoInvitacion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_invitado_id", nullable = true)
    private Usuario usuarioInvitado;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaAceptacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_invitacion_viaje", foreignKeyDefinition = "FOREIGN KEY (viaje_id) REFERENCES viajes(id) ON DELETE CASCADE"))
    private Viaje viaje;
}
