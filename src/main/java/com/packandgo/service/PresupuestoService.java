package com.packandgo.service;

import com.packandgo.dto.PresupuestoDTO;

import java.util.List;

public interface PresupuestoService {
    PresupuestoDTO crearPresupuesto(Long viajeId, PresupuestoDTO dto);
    PresupuestoDTO obtenerPresupuestoPorViaje(Long viajeId);
    PresupuestoDTO actualizarTotalGastado(Long viajeId);
    PresupuestoDTO actualizarPresupuesto(Long viajeId, PresupuestoDTO dto);
    
    void recalcularTotalesGlobales();

    List<PresupuestoDTO> listarTodos();
}
