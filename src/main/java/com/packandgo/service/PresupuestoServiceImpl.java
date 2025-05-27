package com.packandgo.service;

import com.packandgo.dto.PresupuestoDTO;
import com.packandgo.entity.Presupuesto;
import com.packandgo.entity.Viaje;
import com.packandgo.repository.GastoRepository;
import com.packandgo.repository.PresupuestoRepository;
import com.packandgo.repository.ViajeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresupuestoServiceImpl implements PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final ViajeRepository viajeRepository;
    private final GastoRepository gastoRepository;

    @Override
    public PresupuestoDTO crearPresupuesto(Long viajeId, PresupuestoDTO dto) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setViaje(viaje);
        presupuesto.setTotalEstimado(dto.getTotalEstimado());
        presupuesto.setTotalGastado(BigDecimal.ZERO);
        presupuesto.setFechaActualizacion(LocalDateTime.now());

        presupuesto = presupuestoRepository.save(presupuesto);
        return mapToDTO(presupuesto);
    }

    @Override
    public PresupuestoDTO obtenerPresupuestoPorViaje(Long viajeId) {
        Presupuesto presupuesto = presupuestoRepository.findByViajeId(viajeId)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado"));
        return mapToDTO(presupuesto);
    }
    
    @Override
    public PresupuestoDTO actualizarPresupuesto(Long viajeId, PresupuestoDTO dto) {
        Presupuesto presupuesto = presupuestoRepository.findByViajeId(viajeId)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        if (dto.getTotalEstimado() != null) {
            presupuesto.setTotalEstimado(dto.getTotalEstimado());
        }

        presupuesto.setFechaActualizacion(LocalDateTime.now());

        presupuesto = presupuestoRepository.save(presupuesto);
        return mapToDTO(presupuesto);
    }

    @Override
    public PresupuestoDTO actualizarTotalGastado(Long viajeId) {
        Presupuesto presupuesto = presupuestoRepository.findByViajeId(viajeId)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        BigDecimal total = gastoRepository.findByPresupuestoId(presupuesto.getId()).stream()
                .map(g -> g.getCantidad() != null ? g.getCantidad() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        presupuesto.setTotalGastado(total);
        presupuesto.setFechaActualizacion(LocalDateTime.now());
        presupuestoRepository.save(presupuesto);

        return mapToDTO(presupuesto);
    }

    @Override
    public void recalcularTotalesGlobales() {
        List<Presupuesto> presupuestos = presupuestoRepository.findAll();

        for (Presupuesto p : presupuestos) {
            BigDecimal total = gastoRepository.findByPresupuestoId(p.getId()).stream()
                    .map(g -> g.getCantidad() != null ? g.getCantidad() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            p.setTotalGastado(total);
            p.setFechaActualizacion(LocalDateTime.now());
            presupuestoRepository.save(p);
        }
    }

    @Override
    public List<PresupuestoDTO> listarTodos() {
        return presupuestoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PresupuestoDTO mapToDTO(Presupuesto p) {
        PresupuestoDTO dto = new PresupuestoDTO();
        dto.setId(p.getId());
        dto.setViajeId(p.getViaje().getId());
        dto.setTotalEstimado(p.getTotalEstimado());
        dto.setTotalGastado(p.getTotalGastado());
        dto.setFechaActualizacion(p.getFechaActualizacion());
        return dto;
    }
}
