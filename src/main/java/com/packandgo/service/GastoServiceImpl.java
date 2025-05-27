package com.packandgo.service;

import com.packandgo.dto.GastoDTO;
import com.packandgo.entity.Gasto;
import com.packandgo.entity.Presupuesto;
import com.packandgo.repository.GastoRepository;
import com.packandgo.repository.PresupuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final PresupuestoRepository presupuestoRepository;

    @Override
    public GastoDTO agregarGasto(GastoDTO dto) {
        Presupuesto presupuesto = presupuestoRepository.findById(dto.getPresupuestoId())
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        Gasto gasto = new Gasto();
        gasto.setPresupuesto(presupuesto);
        gasto.setConcepto(dto.getConcepto());
        gasto.setCantidad(dto.getCantidad());
        gasto.setPagadoPor(dto.getPagadoPor());
        gasto.setFechaGasto(dto.getFechaGasto());

        gasto = gastoRepository.save(gasto);


        BigDecimal nuevoTotal = presupuesto.getTotalGastado().add(dto.getCantidad());
        presupuesto.setTotalGastado(nuevoTotal);
        presupuesto.setFechaActualizacion(java.time.LocalDateTime.now());
        presupuestoRepository.save(presupuesto);

        return mapToDTO(gasto);
    }

    @Override
    public List<GastoDTO> listarGastosPorPresupuesto(Long presupuestoId) {
        return gastoRepository.findByPresupuestoId(presupuestoId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    @Override
    public GastoDTO actualizarGasto(Long id, GastoDTO dto) {
        Gasto gastoExistente = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        BigDecimal cantidadAnterior = gastoExistente.getCantidad();
        BigDecimal cantidadNueva = dto.getCantidad();
        BigDecimal diferencia = cantidadNueva.subtract(cantidadAnterior);

        gastoExistente.setConcepto(dto.getConcepto());
        gastoExistente.setCantidad(cantidadNueva);
        gastoExistente.setPagadoPor(dto.getPagadoPor());
        gastoExistente.setFechaGasto(dto.getFechaGasto());

        gastoExistente = gastoRepository.save(gastoExistente);

        // Ajustar presupuesto
        Presupuesto presupuesto = gastoExistente.getPresupuesto();
        presupuesto.setTotalGastado(presupuesto.getTotalGastado().add(diferencia));
        presupuesto.setFechaActualizacion(java.time.LocalDateTime.now());
        presupuestoRepository.save(presupuesto);

        return mapToDTO(gastoExistente);
    }

    @Override
    public void eliminarGasto(Long id) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        Presupuesto presupuesto = gasto.getPresupuesto();
        BigDecimal cantidad = gasto.getCantidad();

        gastoRepository.delete(gasto);

        presupuesto.setTotalGastado(presupuesto.getTotalGastado().subtract(cantidad));
        presupuesto.setFechaActualizacion(java.time.LocalDateTime.now());
        presupuestoRepository.save(presupuesto);
    }

    @Override
    public List<GastoDTO> obtenerGastosEntreFechas(LocalDate desde, LocalDate hasta) {
        return gastoRepository.findByFechaGastoBetween(desde, hasta)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private GastoDTO mapToDTO(Gasto g) {
        GastoDTO dto = new GastoDTO();
        dto.setId(g.getId());
        dto.setPresupuestoId(g.getPresupuesto().getId());
        dto.setConcepto(g.getConcepto());
        dto.setCantidad(g.getCantidad());
        dto.setPagadoPor(g.getPagadoPor());
        dto.setFechaGasto(g.getFechaGasto());
        return dto;
    }
}
