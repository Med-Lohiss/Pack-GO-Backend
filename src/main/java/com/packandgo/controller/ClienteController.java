package com.packandgo.controller;

import com.packandgo.dto.*;
import com.packandgo.entity.Usuario;
import com.packandgo.entity.Viaje;
import com.packandgo.enums.EstadoInvitacion;
import com.packandgo.repository.ViajeRepository;
import com.packandgo.repository.InvitacionRepository;
import com.packandgo.service.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final ViajeService viajeService;
    private final ActividadService actividadService;
    private final PexelsService pexelsService;
    private final PresupuestoService presupuestoService;
    private final GastoService gastoService;
    private final InvitacionService invitacionService;
    private final InvitacionRepository invitacionRepository; 
    private final NotificacionService notificacionService;
    private final EmailService emailService;
    private final ViajeRepository viajeRepository;
    private final VotoActividadService votoActividadService;
    private final ChatMensajeService chatMensajeService;
    private final ComentarioService comentarioService;

    // Perfil
    @GetMapping("/perfil")
    public ResponseEntity<ClienteDTO> getPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ClienteDTO cliente = clienteService.obtenerPerfilCliente(email);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/perfil")
    public ResponseEntity<ClienteDTO> actualizarPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody ClienteDTO datosActualizados) {
        String email = userDetails.getUsername();
        ClienteDTO actualizado = clienteService.actualizarPerfilCliente(email, datosActualizados);
        return ResponseEntity.ok(actualizado);
    }

    // Viajes
    @PostMapping("/viajes")
    public ResponseEntity<ViajeDTO> crearViaje(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody ViajeDTO viajeDTO) {
        String email = userDetails.getUsername();
        ViajeDTO creado = viajeService.crearViaje(email, viajeDTO);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/viajes")
    public ResponseEntity<List<ViajeDTO>> listarViajes(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<ViajeDTO> viajes = viajeService.listarViajesUsuario(email);
        return ResponseEntity.ok(viajes);
    }

    @GetMapping("/viajes/{viajeId}")
    public ResponseEntity<ViajeDTO> obtenerViajePorId(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable Long viajeId) {
        String email = userDetails.getUsername();
        ViajeDTO viaje = viajeService.obtenerViajePorId(email, viajeId);
        return ResponseEntity.ok(viaje);
    }

    @PutMapping("/viajes/{viajeId}")
    public ResponseEntity<ViajeDTO> editarViaje(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long viajeId, @RequestBody ViajeDTO viajeDTO) {
        String email = userDetails.getUsername();
        ViajeDTO actualizado = viajeService.editarViaje(email, viajeId, viajeDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/viajes/{viajeId}")
    public ResponseEntity<Void> eliminarViaje(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long viajeId) {
        String email = userDetails.getUsername();
        viajeService.eliminarViaje(email, viajeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/viajes-compartidos")
    public ResponseEntity<List<ViajeDTO>> obtenerViajesCompartidos(@AuthenticationPrincipal Usuario usuario) {
        List<Viaje> viajes = invitacionService.obtenerViajesCompartidos(usuario);
        List<ViajeDTO> viajesDTO = viajes.stream()
                .map(invitacionService::convertirAViajeDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(viajesDTO);
    }

    // Actividades
    @PostMapping("/viajes/{viajeId}/actividades")
    public ResponseEntity<ActividadDTO> crearActividad(@PathVariable Long viajeId,
                                                       @RequestBody ActividadDTO actividadDTO) {
        actividadDTO.setViajeId(viajeId);
        ActividadDTO creada = actividadService.crearActividad(actividadDTO);
        return ResponseEntity.ok(creada);
    }

    @GetMapping("/viajes/{viajeId}/actividades")
    public ResponseEntity<List<ActividadDTO>> listarActividadesPorViaje(@PathVariable Long viajeId) {
        List<ActividadDTO> actividades = actividadService.obtenerActividadesPorViaje(viajeId);
        return ResponseEntity.ok(actividades);
    }

    @GetMapping("/actividades/{actividadId}")
    public ResponseEntity<ActividadDTO> obtenerActividad(@PathVariable Long actividadId) {
        ActividadDTO actividad = actividadService.obtenerActividadPorId(actividadId);
        return ResponseEntity.ok(actividad);
    }

    @PutMapping("/actividades/{actividadId}")
    public ResponseEntity<ActividadDTO> editarActividad(@PathVariable Long actividadId,
                                                        @RequestBody ActividadDTO actividadDTO) {
        ActividadDTO actualizada = actividadService.actualizarActividad(actividadId, actividadDTO);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/actividades/{actividadId}")
    public ResponseEntity<Void> eliminarActividad(@PathVariable Long actividadId) {
        actividadService.eliminarActividad(actividadId);
        return ResponseEntity.noContent().build();
    }

    // Pexels
    @GetMapping("/pexels-imagen")
    public ResponseEntity<Map<String, String>> obtenerImagenDePexels(@RequestParam String ubicacion) {
        try {
            String imagenUrl = pexelsService.obtenerImagenPorUbicacion(ubicacion);
            if (imagenUrl != null) {
                Map<String, String> response = new HashMap<>();
                response.put("url", imagenUrl);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Presupuesto
    @PostMapping("/viajes/{viajeId}/presupuesto")
    public ResponseEntity<PresupuestoDTO> crearPresupuesto(@PathVariable Long viajeId,
                                                           @RequestBody PresupuestoDTO dto) {
        PresupuestoDTO creado = presupuestoService.crearPresupuesto(viajeId, dto);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/viajes/{viajeId}/presupuesto")
    public ResponseEntity<PresupuestoDTO> obtenerPresupuesto(@PathVariable Long viajeId) {
        PresupuestoDTO presupuesto = presupuestoService.obtenerPresupuestoPorViaje(viajeId);
        return ResponseEntity.ok(presupuesto);
    }

    @PutMapping("/viajes/{viajeId}/presupuesto")
    public ResponseEntity<PresupuestoDTO> actualizarPresupuesto(@PathVariable Long viajeId,
                                                                 @RequestBody PresupuestoDTO dto) {
        PresupuestoDTO actualizado = presupuestoService.actualizarPresupuesto(viajeId, dto);
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/viajes/{viajeId}/presupuesto/actualizar")
    public ResponseEntity<PresupuestoDTO> actualizarTotalGastado(@PathVariable Long viajeId) {
        PresupuestoDTO actualizado = presupuestoService.actualizarTotalGastado(viajeId);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/presupuestos")
    public ResponseEntity<List<PresupuestoDTO>> listarTodosLosPresupuestos() {
        return ResponseEntity.ok(presupuestoService.listarTodos());
    }

    // Gastos
    @PostMapping("/gastos")
    public ResponseEntity<GastoDTO> agregarGasto(@RequestBody GastoDTO dto) {
        GastoDTO creado = gastoService.agregarGasto(dto);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/presupuestos/{presupuestoId}/gastos")
    public ResponseEntity<List<GastoDTO>> listarGastosPorPresupuesto(@PathVariable Long presupuestoId) {
        List<GastoDTO> gastos = gastoService.listarGastosPorPresupuesto(presupuestoId);
        return ResponseEntity.ok(gastos);
    }

    @PutMapping("/gastos/{id}")
    public ResponseEntity<GastoDTO> actualizarGasto(@PathVariable Long id, @RequestBody GastoDTO dto) {
        GastoDTO actualizado = gastoService.actualizarGasto(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/gastos/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        gastoService.eliminarGasto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gastos/entre-fechas")
    public ResponseEntity<List<GastoDTO>> obtenerGastosEntreFechas(@RequestParam("desde") String desdeStr,
                                                                    @RequestParam("hasta") String hastaStr) {
        LocalDate desde = LocalDate.parse(desdeStr);
        LocalDate hasta = LocalDate.parse(hastaStr);
        List<GastoDTO> gastos = gastoService.obtenerGastosEntreFechas(desde, hasta);
        return ResponseEntity.ok(gastos);
    }

    // Invitaciones
    @PostMapping("/viajes/{viajeId}/invitaciones")
    public ResponseEntity<InvitacionDTO> crearInvitacion(@AuthenticationPrincipal Usuario usuario,
                                                         @PathVariable Long viajeId,
                                                         @RequestBody InvitacionDTO dto) {
        dto.setViajeId(viajeId);
        InvitacionDTO invitacionCreada = invitacionService.crearInvitacion(dto);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        String UbicacionViaje = viaje.getUbicacion();

        String nombreInvitador = usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail();
        emailService.enviarInvitacionViaje(dto.getEmailInvitado(), nombreInvitador, UbicacionViaje, invitacionCreada.getToken());

        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setUsuarioId(usuario.getId());
        notificacion.setEmailDestino(dto.getEmailInvitado());
        notificacion.setContenido("Has enviado una invitación a " + dto.getEmailInvitado());

        notificacionService.crearNotificacion(notificacion);

        return ResponseEntity.ok(invitacionCreada);
    }

    @PostMapping("/invitaciones/aceptar")
    public ResponseEntity<InvitacionDTO> aceptarInvitacion(@AuthenticationPrincipal Usuario usuario,
                                                           @RequestParam String token) {
        InvitacionDTO aceptada = invitacionService.aceptarInvitacion(token, usuario);
        return ResponseEntity.ok(aceptada);
    }

    @GetMapping("/invitaciones/public/aceptar")
    public ResponseEntity<?> aceptarInvitacionSinAuth(@RequestParam("token") String token) {
        try {
            ViajeDTO viajeDTO = invitacionService.aceptarInvitacionSinAuth(token);
            return ResponseEntity.ok(viajeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/invitaciones/{token}")
    public ResponseEntity<InvitacionDTO> obtenerInvitacion(@PathVariable String token) {
        InvitacionDTO invitacion = invitacionService.obtenerPorToken(token);
        return ResponseEntity.ok(invitacion);
    }

    @GetMapping("/invitaciones/viaje-por-token/{token}")
    public ResponseEntity<ViajeDTO> obtenerViajePorToken(@PathVariable String token) {
        ViajeDTO viaje = invitacionService.obtenerViajePorToken(token);
        return ResponseEntity.ok(viaje);
    }
    
    @GetMapping("/viajes/{viajeId}/invitaciones/existe")
    public ResponseEntity<Boolean> verificarInvitacionExistente(@PathVariable Long viajeId,
                                                                @RequestParam String email) {
        boolean yaInvitado = invitacionRepository.existsByViajeIdAndEmailInvitadoAndEstadoIn(
            viajeId,
            email,
            List.of(EstadoInvitacion.PENDIENTE, EstadoInvitacion.ACEPTADA)
        );

        return ResponseEntity.ok(yaInvitado);
    }


    // Notificaciones
    @PostMapping("/notificaciones")
    public ResponseEntity<NotificacionDTO> crearNotificacion(@RequestBody NotificacionDTO dto) {
        NotificacionDTO creada = notificacionService.crearNotificacion(dto);
        return ResponseEntity.ok(creada);
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionDTO>> obtenerNotificaciones(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Long usuarioId = clienteService.obtenerPerfilCliente(email).getId();
        List<NotificacionDTO> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    @PatchMapping("/notificaciones/{id}/leida")
    public ResponseEntity<Void> marcarNotificacionComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeido(id);
        return ResponseEntity.noContent().build();
    }

    // Votos en Actividades 
    @PostMapping("/actividades/{actividadId}/votar")
    public ResponseEntity<VotoActividadDTO> votarActividad(@AuthenticationPrincipal UserDetails userDetails,
                                                            @PathVariable Long actividadId,
                                                            @RequestBody Map<String, Double> body) {
        Long usuarioId = clienteService.obtenerPerfilCliente(userDetails.getUsername()).getId();
        Double valor = body.get("valor");
        VotoActividadDTO voto = votoActividadService.votarActividad(usuarioId, actividadId, valor);
        return ResponseEntity.ok(voto);
    }

    @GetMapping("/actividades/{actividadId}/promedio-votos")
    public ResponseEntity<Double> obtenerPromedioVotos(@PathVariable Long actividadId) {
        Double promedio = votoActividadService.obtenerPromedioVotos(actividadId);
        return ResponseEntity.ok(promedio);
    }

    @GetMapping("/actividades/{actividadId}/voto-usuario")
    public ResponseEntity<VotoActividadDTO> obtenerVotoDeUsuario(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @PathVariable Long actividadId) {
        Long usuarioId = clienteService.obtenerPerfilCliente(userDetails.getUsername()).getId();
        return votoActividadService.obtenerVotoDeUsuario(usuarioId, actividadId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Chat Mensajes
    @PostMapping("/chat")
    public ResponseEntity<ChatMensajeDTO> enviarMensaje(@RequestBody ChatMensajeDTO dto) {
        return ResponseEntity.ok(chatMensajeService.guardarMensaje(dto));
    }

    @GetMapping("/chat/viaje/{viajeId}")
    public ResponseEntity<List<ChatMensajeDTO>> obtenerMensajes(@PathVariable Long viajeId) {
        return ResponseEntity.ok(chatMensajeService.obtenerMensajesPorViaje(viajeId));
    }
    
    // Comentarios
    @PostMapping("/comentarios")
    public ResponseEntity<ComentarioDTO> crearComentario(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        
        String email = userDetails.getUsername();
        String contenido = body.get("contenido");

        if (contenido == null || contenido.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ComentarioDTO creado = comentarioService.crearComentario(email, contenido);
        return ResponseEntity.ok(creado);
    }

}
