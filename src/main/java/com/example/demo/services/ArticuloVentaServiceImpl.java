package com.example.demo.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.models.ArticuloModel;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.models.VentaModel;
import com.example.demo.repositories.ArticuloRepository;
import com.example.demo.repositories.ArticuloVentaRepository;
import com.example.demo.repositories.VentaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ArticuloVentaServiceImpl implements IArticuloVentaService{

    @Autowired
    private ArticuloVentaRepository articuloVentaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Override
    public boolean eliminarArticuloVentaPorId(Long id) {
        if(articuloVentaRepository.existsById(id)){
            articuloVentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArticuloVentaDTO editarArticuloVenta(ArticuloVentaDTO articuloVentaDTO) {
        // Obtener ArticuloVentaModel con ID
        ArticuloVentaModel articuloVentaModel = articuloVentaRepository.findById(articuloVentaDTO.getId()).
            orElseThrow(() -> new EntityNotFoundException("ArticuloVenta no encontrado"));

        // Setear informacion de ArticuloVentaDTO a ArticuloVentaModel
        articuloVentaModel.setCantidad(articuloVentaDTO.getCantidad());
        articuloVentaModel.setSubtotal(articuloVentaDTO.getSubtotal());

        // Obtener y asignar Articulo a ArticuloVenta
        ArticuloModel articuloModel = articuloRepository.findById(articuloVentaDTO.getArticulo().getId())
            .orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado"));
        articuloVentaModel.setArticulo(articuloModel);
        
        // Persistir ArticuloVentaModel con la nueva informacion
        ArticuloVentaModel guardado = articuloVentaRepository.save(articuloVentaModel);

        // Mapear ArticuloVentaModel a ArticuloVentaDTO y retornar
        return modelMapper.map(guardado, ArticuloVentaDTO.class);
    }

    @Override
    public ArticuloVentaDTO agregarArticuloVenta(ArticuloVentaDTO articuloVentaDTO, Long id) {
        // Setear informacion del ArticuloVentaDTO a ArticuloVentaModel
        ArticuloVentaModel articuloVentaModel = new ArticuloVentaModel();
        articuloVentaModel.setCantidad(articuloVentaDTO.getCantidad());
        articuloVentaModel.setSubtotal(articuloVentaDTO.getSubtotal());

        // Obtener y asignar ArticuloModel a ArticuloVentaModel
        ArticuloModel articuloModel = articuloRepository.findById(articuloVentaDTO.getArticulo().getId())
            .orElseThrow(() -> new EntityNotFoundException("Articulo no encontrado"));
        articuloVentaModel.setArticulo(articuloModel);

        // Obtener y asignar VentaModel a ArticuloVentaModel
        VentaModel ventaModel = ventaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        articuloVentaModel.setVenta(ventaModel);

        // Persistir ArticuloVentaModel
        ArticuloVentaModel guardado = articuloVentaRepository.save(articuloVentaModel);

        // Mapear de ArticuloVentaModel a ArticuloVentaDTO
        return modelMapper.map(guardado, ArticuloVentaDTO.class);
    }
    
}
