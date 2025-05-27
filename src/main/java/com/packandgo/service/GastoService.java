package com.packandgo.service;

import com.packandgo.dto.GastoDTO;

import java.time.LocalDate;
import java.util.List;

public interface GastoService {
	GastoDTO agregarGasto(GastoDTO dto);

    List<GastoDTO> listarGastosPorPresupuesto(Long presupuestoId);

    List<GastoDTO> obtenerGastosEntreFechas(LocalDate desde, LocalDate hasta);
    
    GastoDTO actualizarGasto(Long id, GastoDTO dto);
    
    void eliminarGasto(Long id);
}
