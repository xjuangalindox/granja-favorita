package com.example.demo.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.RazaModel;
import com.example.demo.repositories.RazaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RazaServiceImpl implements IRazaService{

    @Autowired
    private RazaRepository razaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RazaDTO> obtenerRazas() {
        List<RazaModel> listaRazas = (List<RazaModel>) razaRepository.findAll();
        listaRazas.sort(Comparator.comparing(item -> item.getNombre()));

        return listaRazas.stream()
                .map(item -> modelMapper.map(item, RazaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RazaDTO obtenerRazaPorId(Long id) {
        RazaModel razaModel = razaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Raza no encontrada"));

        return modelMapper.map(razaModel, RazaDTO.class);
    }

    @Override
    public RazaDTO guardarRaza(RazaDTO razaDTO) {
        RazaModel razaModel = razaRepository.save(modelMapper.map(razaDTO, RazaModel.class));

        return modelMapper.map(razaModel, RazaDTO.class);
    }

    @Override
    public RazaDTO editarRaza(Long id, RazaDTO razaDTO) {
        RazaModel razaModel = modelMapper.map(razaDTO, RazaModel.class);
        razaModel.setId(id);

        RazaModel guardado = razaRepository.save(razaModel);

        return modelMapper.map(guardado, RazaDTO.class);
    }

    @Override
    public boolean eliminarRazaPorId(Long id) {
        if(razaRepository.existsById(id)){
            razaRepository.deleteById(id);
            return true;
        }

        return false;
    }
    
}
