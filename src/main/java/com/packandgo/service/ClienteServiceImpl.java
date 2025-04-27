package com.packandgo.service;

import com.packandgo.dto.ClienteDTO;
import com.packandgo.entity.Cliente;
import com.packandgo.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepository;

	@Override
	public ClienteDTO obtenerPerfilCliente(String email) {
		Cliente cliente = clienteRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));

		return mapToDTO(cliente);
	}

	@Override
	public ClienteDTO actualizarPerfilCliente(String email, ClienteDTO datosActualizados) {
		Cliente cliente = clienteRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));

		cliente.setNombre(datosActualizados.getNombre());
		cliente.setApellido1(datosActualizados.getApellido1());
		cliente.setApellido2(datosActualizados.getApellido2());
		cliente.setDni(datosActualizados.getDni());
		cliente.setTelefono(datosActualizados.getTelefono());
		cliente.setDomicilio(datosActualizados.getDomicilio());
		cliente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
		cliente.setMetodoPago(datosActualizados.getMetodoPago());
		cliente.setNotificaciones(datosActualizados.isNotificaciones());

		clienteRepository.save(cliente);

		return mapToDTO(cliente);
	}

	private ClienteDTO mapToDTO(Cliente cliente) {
		ClienteDTO dto = new ClienteDTO();
		dto.setId(cliente.getId());
		dto.setNombre(cliente.getNombre());
		dto.setEmail(cliente.getEmail());
		dto.setRolUsuario(cliente.getRolUsuario());
		dto.setProvider(cliente.getProvider());
		dto.setFechaCreacion(cliente.getFechaCreacion());
		dto.setFechaBaja(cliente.getFechaBaja());

		dto.setApellido1(cliente.getApellido1());
		dto.setApellido2(cliente.getApellido2());
		dto.setDni(cliente.getDni());
		dto.setTelefono(cliente.getTelefono());
		dto.setDomicilio(cliente.getDomicilio());
		dto.setFechaNacimiento(cliente.getFechaNacimiento());
		dto.setMetodoPago(cliente.getMetodoPago());
		dto.setNotificaciones(cliente.isNotificaciones());

		return dto;
	}
}
