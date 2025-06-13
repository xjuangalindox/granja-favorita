package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;

public interface INacimientoService {
    
    // Obtener montas
    public List<NacimientoDTO> obtenerNacimientos();

    public List<NacimientoDTO> obtenerNacimientosDisponibles();

    // Obtener por id monta
    public NacimientoDTO obtenerNacimientoPorIdMonta(Long id);

    // Obtener monta por id
    public Optional<NacimientoDTO> obtenerNacimientoById(Long id);

    // Guardar monta
    public NacimientoDTO guardarNacimiento(NacimientoDTO nacimientoDTO);

    // Editar monta
    public NacimientoDTO editarNacimiento(Long id, NacimientoDTO nacimientoDTO, List<Long> ejemplaresEliminados);
    
    // Eliminar monta por id
    public boolean eliminarNacimientoById(Long id);

    public boolean existsById(Long id);

    // Editar nacimiento desde: "/montas"
    public Optional<NacimientoDTO> findByMonta(MontaDTO montaDTO);
}
