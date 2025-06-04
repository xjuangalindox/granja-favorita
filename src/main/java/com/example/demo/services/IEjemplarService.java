package com.example.demo.services;

import java.util.Optional;

import com.example.demo.controllers.dto.EjemplarDTO;

public interface IEjemplarService {

    public Optional<EjemplarDTO> obtenerEjemplarPorId(Long id);

    public EjemplarDTO guardarEjemplar(EjemplarDTO ejemplarDTO);

    public boolean eliminarEjemplarPorId(Long id);

    public EjemplarDTO editarEjemplar(EjemplarDTO ejemplarDTO);

    public EjemplarDTO agregarEjemplar(EjemplarDTO ejemplarDTO, Long id);
}
