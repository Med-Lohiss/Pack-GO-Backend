package com.packandgo.service;

import com.packandgo.dto.ActividadDTO;
import com.packandgo.entity.Actividad;
import com.packandgo.entity.Viaje;
import com.packandgo.repository.ActividadRepository;
import com.packandgo.repository.ViajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

	private final ActividadRepository actividadRepository;
	private final ViajeRepository viajeRepository;

	@Override
	public ActividadDTO crearActividad(ActividadDTO actividadDTO) {
		Viaje viaje = viajeRepository.findById(actividadDTO.getViajeId())
				.orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

		Actividad actividad = Actividad.builder().nombre(actividadDTO.getNombre())
				.descripcion(actividadDTO.getDescripcion()).fecha(actividadDTO.getFecha()).hora(actividadDTO.getHora())
				.precio(actividadDTO.getPrecio()).tipoActividad(actividadDTO.getTipoActividad()).viaje(viaje).build();

		Actividad guardada = actividadRepository.save(actividad);
		return convertirADTO(guardada);
	}

	@Override
	public List<ActividadDTO> obtenerActividadesPorViaje(Long viajeId) {
		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

		return actividadRepository.findByViaje(viaje).stream().map(this::convertirADTO).collect(Collectors.toList());
	}

	@Override
	public ActividadDTO obtenerActividadPorId(Long id) {
		Actividad actividad = actividadRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
		return convertirADTO(actividad);
	}

	@Override
	public void eliminarActividad(Long id) {
		actividadRepository.deleteById(id);
	}

	@Override
	public ActividadDTO actualizarActividad(Long id, ActividadDTO actividadDTO) {
		Actividad actividad = actividadRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

		actividad.setNombre(actividadDTO.getNombre());
		actividad.setDescripcion(actividadDTO.getDescripcion());
		actividad.setFecha(actividadDTO.getFecha());
		actividad.setHora(actividadDTO.getHora());
		actividad.setPrecio(actividadDTO.getPrecio());
		actividad.setTipoActividad(actividadDTO.getTipoActividad());

		Actividad actualizada = actividadRepository.save(actividad);
		return convertirADTO(actualizada);
	}

	private ActividadDTO convertirADTO(Actividad actividad) {
		return ActividadDTO.builder().id(actividad.getId()).nombre(actividad.getNombre())
				.descripcion(actividad.getDescripcion()).fecha(actividad.getFecha()).hora(actividad.getHora())
				.precio(actividad.getPrecio()).tipoActividad(actividad.getTipoActividad())
				.viajeId(actividad.getViaje().getId()).build();
	}
}
