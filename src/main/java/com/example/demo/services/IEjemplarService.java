package com.example.demo.services;

import com.example.demo.controllers.dto.EjemplarDTO;

public interface IEjemplarService {
    public boolean eliminarEjemplarPorId(Long id);

    public EjemplarDTO editarEjemplar(EjemplarDTO ejemplarDTO);

    public EjemplarDTO agregarEjemplar(EjemplarDTO ejemplarDTO, Long id);
}
