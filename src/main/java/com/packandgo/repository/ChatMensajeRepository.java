package com.packandgo.repository;

import com.packandgo.entity.ChatMensaje;
import com.packandgo.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {
    List<ChatMensaje> findByViajeOrderByFechaEnvioAsc(Viaje viaje);
}
