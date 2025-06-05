package com.example.demo.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.ArticuloDTO;
import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.repositories.ArticuloVentaRepository;

@Service
public class ArticuloVentaServiceImpl implements IArticuloVentaService{

    @Autowired
    private ArticuloVentaRepository articuloVentaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IArticuloService articuloService;

    @Override
    public boolean eliminarArticuloVentaPorId(Long id) {
        if(articuloVentaRepository.existsById(id)){
            articuloVentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArticuloVentaDTO editarArticuloVenta(Long id, ArticuloVentaDTO articuloVentaDTO) {

        Optional<ArticuloVentaDTO> articuloVentaOpt = obtenerArticuloVentaPorId(id);
        if(articuloVentaOpt.isEmpty()){
            throw new RuntimeException("ArticuloVenta no encontrado.");
        }

        Optional<ArticuloDTO> articuloOpt = articuloService.obtenerPorId(articuloVentaDTO.getArticulo().getId());
        if(articuloOpt.isEmpty()){
            throw new RuntimeException("Articulo no encontrado.");
        }

        ArticuloVentaDTO articuloVenta = articuloVentaOpt.get();
        articuloVenta.setCantidad(articuloVentaDTO.getCantidad());
        articuloVenta.setSubtotal(articuloVentaDTO.getSubtotal());
        articuloVenta.setArticulo(articuloOpt.get());
        //articuloVenta.setVenta(articuloVentaDTO.getVenta());

        ArticuloVentaModel articuloVentaModel = modelMapper.map(articuloVenta, ArticuloVentaModel.class);
        articuloVentaModel = articuloVentaRepository.save(articuloVentaModel);

        return modelMapper.map(articuloVentaModel, ArticuloVentaDTO.class);
    }

    @Override
    public ArticuloVentaDTO guardarArticuloVenta(ArticuloVentaDTO articuloVentaDTO) {
        
        Optional<ArticuloDTO> articuloOpt = articuloService.obtenerPorId(articuloVentaDTO.getArticulo().getId());
        if(articuloOpt.isEmpty()){
            throw new RuntimeException("Articulo no encontrado");
        }

        ArticuloVentaDTO articuloVenta = new ArticuloVentaDTO();
        articuloVenta.setCantidad(articuloVentaDTO.getCantidad());
        articuloVenta.setSubtotal(articuloVentaDTO.getSubtotal());
        articuloVenta.setArticulo(articuloOpt.get());
        articuloVenta.setVenta(articuloVentaDTO.getVenta());

        ArticuloVentaModel articuloVentaModel = modelMapper.map(articuloVenta, ArticuloVentaModel.class);
        articuloVentaModel = articuloVentaRepository.save(articuloVentaModel);
        
        return modelMapper.map(articuloVentaModel, ArticuloVentaDTO.class);
    }

    @Override
    public Optional<ArticuloVentaDTO> obtenerArticuloVentaPorId(Long id) {
        return articuloVentaRepository.findById(id)
            .map(model -> modelMapper.map(model, ArticuloVentaDTO.class));
    }
    
}
