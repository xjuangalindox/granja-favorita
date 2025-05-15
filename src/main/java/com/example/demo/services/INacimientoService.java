package com.example.demo.services;

import java.util.List;

import com.example.demo.controllers.dto.NacimientoDTO;

public interface INacimientoService {
    
    // Obtener montas
    public List<NacimientoDTO> obtenerNacimientos();

    // Obtener por id monta
    public NacimientoDTO obtenerNacimientoPorIdMonta(Long id);

    // Obtener monta por id
    public NacimientoDTO obtenerNacimientoById(Long id);

    // Guardar monta
    public NacimientoDTO guardarNacimiento(NacimientoDTO nacimientoDTO);

    // Editar monta
    public NacimientoDTO editarNacimiento(Long id, NacimientoDTO nacimientoDTO);

    // Eliminar monta por id
    public boolean eliminarNacimientoById(Long id);
}
