package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.controllers.dto.MontaDTO;

public interface IMontaService {
    
    // Obtener montas
    public List<MontaDTO> obtenerMontas();

    // Obtener monta por id
    public Optional<MontaDTO> obtenerMontaById(Long id);

    // Guardar monta
    public MontaDTO guardarMonta(MontaDTO montaDTO);

    // Editar monta
    public MontaDTO editarMonta(Long id, MontaDTO montaDTO);

    // Eliminar monta por id
    public boolean eliminarMontaById(Long id);

    public boolean existsById(Long id);
}
