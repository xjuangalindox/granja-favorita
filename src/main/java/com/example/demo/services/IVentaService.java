package com.example.demo.services;

import java.util.List;

import com.example.demo.controllers.dto.VentaDTO;

public interface IVentaService {
    
    public List<VentaDTO> obtenerVentas();

    public VentaDTO obtenerVentaPorId(Long id);

    public VentaDTO guardarVenta(VentaDTO ventaDTO);

    public VentaDTO actualizarDatosPrincipales(Long id, VentaDTO ventaDTO);

    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO);

    public boolean eliminarVenta(Long id);
}
