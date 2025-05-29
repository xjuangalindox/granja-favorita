package com.example.demo.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.repositories.ConejoRepository;

@Service
public class ConejoServiceImpl implements IConejoService{

    @Autowired
    private ConejoRepository conejoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ConejoDTO> buscarPorFiltros(String nombre, String sexo) {
        List<ConejoModel> listaConejos = new ArrayList<>();

        //Retornar todos los conejos
        if((nombre == null || nombre.isEmpty()) && (sexo == null || sexo.isEmpty())){
            listaConejos = (List<ConejoModel>) conejoRepository.findAll();  

        //Retornar todos los conejos que contengan "nombre"
        }else if((nombre != null && !nombre.isEmpty()) && (sexo == null || sexo.isEmpty())){
            listaConejos = conejoRepository.findByNombreContainingIgnoreCase(nombre); 
            
        //Retornar todos los conejos que contengan "Macho" o "Hembra"
        }else if((sexo != null && !sexo.isEmpty()) && (nombre == null || nombre.isEmpty())){
            listaConejos = conejoRepository.findBySexoIgnoreCase(sexo); 

        //Retornar todos los conejos que contengan "nombre" y que contengan "Macho" o "Hembra"
        }else{
            listaConejos = conejoRepository.findByNombreContainingIgnoreCaseAndSexoIgnoreCase(nombre, sexo);
        }
        
        //Transformar cada ConejoModel a ConejoDTO
        return listaConejos.stream()
        .map(item -> modelMapper.map(item, ConejoDTO.class))
        .collect(Collectors.toList());
    }

    @Override
    public List<ConejoDTO> obtenerConejos() {
        List<ConejoModel> entitiesList = (List<ConejoModel>) conejoRepository.findAll();
        entitiesList.sort(Comparator.comparing(item -> item.getNombre()));

        // Conversion entitiesList a dtosList
        List<ConejoDTO> dtosList = entitiesList.stream()
        .map(item -> modelMapper.map(item, ConejoDTO.class))
        .collect(Collectors.toList());

        return dtosList;
    }

    @Override
    public ConejoDTO obtenerConejoById(Long id) {
        ConejoModel conejoModel = conejoRepository.findById(id).orElse(null);

        // Conversion de ConejoModel a ConejoDTO
        ConejoDTO conejoDTO = modelMapper.map(conejoModel, ConejoDTO.class);

        return conejoDTO;
    }

    @Override
    public ConejoDTO guardarConejo(ConejoDTO conejoDTO) {
        //Conversion de ConejoDTO a ConejoModel
        ConejoModel conejoModel = modelMapper.map(conejoDTO, ConejoModel.class);
        // Guardar conejo
        ConejoModel guardado = conejoRepository.save(conejoModel);

        // Retornar ConejoDTO guardado
        return modelMapper.map(guardado, ConejoDTO.class);
    }

    @Override
    public ConejoDTO editarConejo(Long id, ConejoDTO conejoDTO) {
        // Asignar id a ConejoDTO
        conejoDTO.setId(id);

        // Conversion de ConejoDTO a ConejoModel
        ConejoModel conejoModel = modelMapper.map(conejoDTO, ConejoModel.class);
        // Guardar modificacion
        ConejoModel guardado = conejoRepository.save(conejoModel);

        // Retornar ConejoDTO guardado
        return modelMapper.map(guardado, ConejoDTO.class);
    }

    @Override
    public boolean eliminarConejoById(Long id) {
        // Verificar si existe conejo
        if(conejoRepository.existsById(id)){
            // Eliminar conejo
            conejoRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public List<ConejoDTO> obtenerConejosPorSexo(String sexo) {
        List<ConejoModel> entitiesList = conejoRepository.findBySexoIgnoreCase(sexo);
        entitiesList.sort(Comparator.comparing(item -> item.getNombre()));

        List<ConejoDTO> dtosList = entitiesList.stream()
        .map(item -> modelMapper.map(item, ConejoDTO.class))
        .collect(Collectors.toList());

        return dtosList;
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return conejoRepository.existsByNombre(nombre);
    }



}
