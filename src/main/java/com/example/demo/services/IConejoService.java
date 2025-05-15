package com.example.demo.services;

import java.util.List;

import com.example.demo.controllers.dto.ConejoDTO;

public interface IConejoService {

    // buscar por filtros
    public List<ConejoDTO> buscarPorFiltros(String nombre, String sexo);

    // Obtener conejos
    public List<ConejoDTO> obtenerConejos();

    // Obtener conejo por id
    public ConejoDTO obtenerConejoById(Long id);

    // Guardar conejo
    public ConejoDTO guardarConejo(ConejoDTO conejoDTO);

    // Editar conejo
    public ConejoDTO editarConejo(Long id, ConejoDTO conejoDTO);

    // Eliminar conejo por id
    public boolean eliminarConejoById(Long id);

    public List<ConejoDTO> obtenerConejosPorSexo(String sexo);

    public boolean existsByNombre(String nombre);

}
