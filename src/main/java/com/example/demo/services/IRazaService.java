package com.example.demo.services;

import java.util.List;

import com.example.demo.controllers.dto.RazaDTO;

public interface IRazaService {
    
    public List<RazaDTO> obtenerRazas();

    public RazaDTO obtenerRazaPorId(Long id);

    public RazaDTO guardarRaza(RazaDTO razaDTO);

    public RazaDTO editarRaza(Long id, RazaDTO razaDTO);

    public boolean eliminarRazaPorId(Long id);
}
