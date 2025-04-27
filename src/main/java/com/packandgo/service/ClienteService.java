package com.packandgo.service;

import com.packandgo.dto.ClienteDTO;

public interface ClienteService {
	
	ClienteDTO obtenerPerfilCliente(String email);

	ClienteDTO actualizarPerfilCliente(String email, ClienteDTO datosActualizados);
}
