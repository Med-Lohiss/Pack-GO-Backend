package com.packandgo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailDestino;

    private String contenido;

    @Builder.Default
    private Boolean leido = false;

    private LocalDateTime fechaEnvio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notificacion_usuario", foreignKeyDefinition = "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE"))
    private Usuario usuario;
}
