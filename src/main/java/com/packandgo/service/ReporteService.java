package com.packandgo.service;

import com.packandgo.dto.ReporteDTO;

import java.util.List;

public interface ReporteService {
    ReporteDTO crearReporte(ReporteDTO dto);
    ReporteDTO actualizarReporte(Long id, ReporteDTO dto);
    List<ReporteDTO> listarReportes();
    void eliminarReporte(Long id);
    ReporteDTO obtenerReportePorId(Long id);
}
