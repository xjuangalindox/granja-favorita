package com.example.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;

@Service
public class NacimientoServiceImpl implements INacimientoService{

    @Autowired
    private NacimientoRepository nacimientoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MontaRepository montaRepository;

    @Override
    public List<NacimientoDTO> obtenerNacimientos() {
        List<NacimientoModel> entitiesList = (List<NacimientoModel>) nacimientoRepository.findAll();
        
        // Conversion de List<NacimientoModel> a List<NacimientoDTO>
        List<NacimientoDTO> dtosList = entitiesList.stream()
        .map(item -> modelMapper.map(item, NacimientoDTO.class))
        .collect(Collectors.toList());

        return dtosList;
    }

    @Override
    public NacimientoDTO obtenerNacimientoPorIdMonta(Long id) {
        // Obtener MontaModel por id
        MontaModel montaModel = montaRepository.findById(id).orElse(null);
        // Obtener NacimientoModel por MontaModel
        NacimientoModel nacimientoModel = nacimientoRepository.findByMonta(montaModel).orElse(null);

        // Conversion de NacimientoModel a NacimientoDTO
        return modelMapper.map(nacimientoModel, NacimientoDTO.class);
    }

    @Override
    public NacimientoDTO obtenerNacimientoById(Long id) {
        NacimientoModel nacimientoModel = nacimientoRepository.findById(id).orElse(null);

        // Conversion de NacimientoModel a NacimientoDTO
        NacimientoDTO nacimientoDTO = modelMapper.map(nacimientoModel, NacimientoDTO.class);

        return nacimientoDTO;
    }

    @Override
    public NacimientoDTO guardarNacimiento(NacimientoDTO nacimientoDTO) {
        // Conversion de NacimientoDTO a NacimientoModel
        NacimientoModel nacimientoModel = modelMapper.map(nacimientoDTO, NacimientoModel.class);
        // Gardar
        NacimientoModel nacimientoModelGuardado = nacimientoRepository.save(nacimientoModel);
        // Conversion de NacimientoModel a NacimientoDTO
        NacimientoDTO nacimientoDTOGuardado = modelMapper.map(nacimientoModelGuardado, NacimientoDTO.class);

        return nacimientoDTOGuardado;
    }

    @Override
    public NacimientoDTO editarNacimiento(Long id, NacimientoDTO nacimientoDTO) {
        nacimientoDTO.setId(id);
        // Conversion de NacimientoDTO a NacimientoModel
        NacimientoModel nacimientoModel = modelMapper.map(nacimientoDTO, NacimientoModel.class);
        // Guardar
        NacimientoModel nacimientoModelEditado  = nacimientoRepository.save(nacimientoModel);
        // Conversion de NacimientoModel a NacimientoDTO
        NacimientoDTO nacimientoDTOEditado = modelMapper.map(nacimientoModelEditado, NacimientoDTO.class);

        return nacimientoDTOEditado;
    }

    @Override
    public boolean eliminarNacimientoById(Long id) {
        // Verificar si existe el nacimiento
        if(nacimientoRepository.existsById(id)){
            nacimientoRepository.deleteById(id);
            return true;
        }
        return false;
    }


    
}
