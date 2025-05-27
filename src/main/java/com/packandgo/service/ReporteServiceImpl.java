package com.packandgo.service;

import com.packandgo.dto.ReporteDTO;
import com.packandgo.entity.Reporte;
import com.packandgo.entity.Usuario;
import com.packandgo.repository.ReporteRepository;
import com.packandgo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public ReporteDTO crearReporte(ReporteDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioReportanteId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Reporte reporte = new Reporte();
        reporte.setUsuarioReportante(usuario);
        reporte.setContenido(dto.getContenido());
        reporte.setMotivo(dto.getMotivo());

        return convertirAReporteDTO(reporteRepository.save(reporte));
    }

    @Override
    public ReporteDTO actualizarReporte(Long id, ReporteDTO dto) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        reporte.setContenido(dto.getContenido());
        reporte.setMotivo(dto.getMotivo());

        return convertirAReporteDTO(reporteRepository.save(reporte));
    }

    @Override
    public List<ReporteDTO> listarReportes() {
        return reporteRepository.findAll().stream()
                .map(this::convertirAReporteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarReporte(Long id) {
        reporteRepository.deleteById(id);
    }

    @Override
    public ReporteDTO obtenerReportePorId(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
        return convertirAReporteDTO(reporte);
    }

    private ReporteDTO convertirAReporteDTO(Reporte reporte) {
        ReporteDTO dto = new ReporteDTO();
        dto.setId(reporte.getId());
        dto.setUsuarioReportanteId(reporte.getUsuarioReportante().getId());
        dto.setNombreUsuarioReportante(reporte.getUsuarioReportante().getNombre());
        dto.setContenido(reporte.getContenido());
        dto.setMotivo(reporte.getMotivo());
        dto.setFechaReporte(reporte.getFechaReporte());
        return dto;
    }
}

