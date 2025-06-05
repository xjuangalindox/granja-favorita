package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.controllers.dto.ArticuloDTO;

public interface IArticuloService {

    public List<ArticuloDTO> obtenerArticulos();

    public Optional<ArticuloDTO> obtenerPorId(Long id);

    public ArticuloDTO guardarArticulo(ArticuloDTO articuloDTO);

    public ArticuloDTO editarArticulo(Long id, ArticuloDTO articuloDTO);

    public boolean eliminarArticuloPorId(Long id);
}
