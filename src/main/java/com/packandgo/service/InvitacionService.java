package com.packandgo.service;

import com.packandgo.dto.InvitacionDTO;
import com.packandgo.dto.ViajeDTO;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.Viaje;

import java.util.List;

public interface InvitacionService {
    InvitacionDTO crearInvitacion(InvitacionDTO dto);
    InvitacionDTO aceptarInvitacion(String token, Usuario usuario);
    InvitacionDTO obtenerPorToken(String token);

    List<Viaje> obtenerViajesCompartidos(Usuario usuario);
    ViajeDTO convertirAViajeDTO(Viaje viaje);
    
    ViajeDTO obtenerViajePorToken(String token);
    
    boolean usuarioTieneAccesoAViaje(Usuario usuario, Viaje viaje);
    ViajeDTO aceptarInvitacionSinAuth(String token);
    
    void aceptarInvitacionesPendientes(String email, Usuario usuario);
}
