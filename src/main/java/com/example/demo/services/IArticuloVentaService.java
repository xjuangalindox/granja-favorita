package com.example.demo.services;

import com.example.demo.controllers.dto.ArticuloVentaDTO;

public interface IArticuloVentaService {
    public boolean eliminarArticuloVentaPorId(Long id);

    public ArticuloVentaDTO editarArticuloVenta(ArticuloVentaDTO articuloVentaDTO);

    public ArticuloVentaDTO agregarArticuloVenta(ArticuloVentaDTO articuloVentaDTO, Long id);
}
