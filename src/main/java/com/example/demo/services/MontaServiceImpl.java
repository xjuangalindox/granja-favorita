package com.example.demo.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.models.MontaModel;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;

@Service
public class MontaServiceImpl implements IMontaService{

    @Autowired
    private NacimientoRepository nacimientoRepository;

    @Autowired
    private MontaRepository montaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<MontaDTO> obtenerMontas() {
        List<MontaModel> entitiesList = (List<MontaModel>) montaRepository.findAll();
        entitiesList.sort(Comparator.comparing(item -> item.getFechaMonta()));

        // Conversion de List<MontaModel> a List<MontaDTO>
        List<MontaDTO> dtosList = entitiesList.stream()
        .map(item -> modelMapper.map(item, MontaDTO.class))
        .collect(Collectors.toList());

        // ¿La monta tiene nacimiento?
        for(MontaDTO item : dtosList){
            item.setTieneNacimiento(nacimientoRepository.existsByMontaId(item.getId()));
        }

        return dtosList;
    }

    @Override
    public Optional<MontaDTO> obtenerMontaById(Long id) {
        return montaRepository.findById(id)
            .map(model -> modelMapper.map(model, MontaDTO.class));
    }

    @Override
    public MontaDTO guardarMonta(MontaDTO montaDTO) {
        // Converion de MontaDTO a MontaModel
        MontaModel montaModel = modelMapper.map(montaDTO, MontaModel.class);
        //Guardar
        MontaModel montaModelGuardado = montaRepository.save(montaModel);
        // Conversion de MontaModel a MontaDTO
        MontaDTO montaDTOGuardado = modelMapper.map(montaModelGuardado, MontaDTO.class);

        return montaDTOGuardado;
    }

    @Override
    public MontaDTO editarMonta(Long id, MontaDTO montaDTO) {
        montaDTO.setId(id);
        // Conversion de MontaDTO a MontaModel
        MontaModel montaModel = modelMapper.map(montaDTO, MontaModel.class);
        //Editar
        MontaModel montaModelEditado = montaRepository.save(montaModel);
        // Conversion de MontaModel a MontaDTO
        MontaDTO montaDTOEditado = modelMapper.map(montaModelEditado, MontaDTO.class);

        return montaDTOEditado;
    }

    @Override
    public boolean eliminarMontaById(Long id) {
        // Verificar si existe monta
        if(montaRepository.existsById(id)){
            montaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        return montaRepository.existsById(id);
    }
    
}
