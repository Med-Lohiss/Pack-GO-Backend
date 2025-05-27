package com.packandgo.controller;

import com.packandgo.dto.ActividadDTO;
import com.packandgo.dto.ComentarioDTO;
import com.packandgo.dto.ViajeDTO;
import com.packandgo.service.ActividadService;
import com.packandgo.service.ComentarioService;
import com.packandgo.service.ViajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final ViajeService viajeService;
    private final ActividadService actividadService;
    private final ComentarioService comentarioService;

    @GetMapping("/viajes/publicados")
    public List<ViajeDTO> obtenerViajesCompartidos() {
        return viajeService.listarViajesCompartidos();
    }

    @GetMapping("/viajes/{viajeId}/actividades")
    public List<ActividadDTO> obtenerActividadesPorViaje(@PathVariable Long viajeId) {
        return actividadService.obtenerActividadesPorViaje(viajeId);
    }

    @GetMapping("/comentarios/aprobados")
    public List<ComentarioDTO> obtenerComentariosAprobados() {
        return comentarioService.obtenerComentariosAprobados();
    }
}
