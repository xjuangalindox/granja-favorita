package com.example.demo.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.ArticuloDTO;
import com.example.demo.models.ArticuloModel;
import com.example.demo.repositories.ArticuloRepository;

@Service
public class ArticuloServiceImpl implements IArticuloService{

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ArticuloDTO> obtenerArticulos() {
        List<ArticuloModel> listaArticulos = (List<ArticuloModel>) articuloRepository.findAll();
        listaArticulos.sort(Comparator.comparing(item -> item.getNombre()));
        
        //Tranformar de List<ArticuloModel> a List<ArticuloDTO>
        return listaArticulos.stream()
            .map(item -> modelMapper.map(item, ArticuloDTO.class))
            .collect(Collectors.toList());    
    }

    @Override
    public Optional<ArticuloDTO> obtenerPorId(Long id) {
        return articuloRepository.findById(id)
            .map(model -> modelMapper.map(model, ArticuloDTO.class));
    }

    @Override
    public ArticuloDTO guardarArticulo(ArticuloDTO articuloDTO) {
        //Tranformar de ArticuloDTO a ArticuloModel
        ArticuloModel articuloModel = modelMapper.map(articuloDTO, ArticuloModel.class);

        ArticuloModel articuloModelGuardado = articuloRepository.save(articuloModel);

        //Transformar de ArticuloModel a ArticuloDTO
        return modelMapper.map(articuloModelGuardado, ArticuloDTO.class);
    }

    @Override
    public ArticuloDTO editarArticulo(Long id, ArticuloDTO articuloDTO) {
        //Tranformar de ArticuloDTO a ArticuloModel
        ArticuloModel articuloModel = modelMapper.map(articuloDTO, ArticuloModel.class);
        articuloModel.setId(id);

        ArticuloModel articuloModelEditado = articuloRepository.save(articuloModel);

        //Transformar de ArticuloModel a ArticuloDTO
        return modelMapper.map(articuloModelEditado, ArticuloDTO.class);
    }

    @Override
    public boolean eliminarArticuloPorId(Long id) {
        if(articuloRepository.existsById(id)){
            articuloRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
}
