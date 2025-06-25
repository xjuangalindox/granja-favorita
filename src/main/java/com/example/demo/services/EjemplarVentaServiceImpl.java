package com.example.demo.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.EjemplarVentaDTO;
import com.example.demo.models.EjemplarVentaModel;
import com.example.demo.repositories.EjemplarVentaRepository;

@Service
public class EjemplarVentaServiceImpl implements IEjemplarVentaService{

    @Autowired
    private EjemplarVentaRepository ejemplarVentaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IEjemplarService ejemplarService;

    @Override
    public boolean eliminarEjemplarVentaPorId(Long id) {
        if(ejemplarVentaRepository.existsById(id)){
            ejemplarVentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public EjemplarVentaDTO guardarEjemplarVenta(EjemplarVentaDTO ejemplarVentaDTO) {

        // Obtener ejemplar del ejemplar venta
        Optional<EjemplarDTO> ejemplarOpt = ejemplarService.obtenerEjemplarPorId(ejemplarVentaDTO.getEjemplar().getId());
        if(ejemplarOpt.isEmpty()){
            throw new RuntimeException("Ejemplar no encontrado");
        }

        // Persistir nuevo estatus del ejemplar
        EjemplarDTO ejemplarDTO = ejemplarOpt.get();
        ejemplarDTO.setVendido(ejemplarVentaDTO.getEjemplar().isVendido());
        ejemplarDTO = ejemplarService.editarEjemplar(ejemplarDTO);
        //ejemplarDTO.setVendido(true);
        //ejemplarDTO = ejemplarService.guardarEjemplar(ejemplarDTO);

        // Asigar ejemplar a ejemplar venta y persistir
        ejemplarVentaDTO.setEjemplar(ejemplarDTO);
        /*EjemplarVentaDTO ejemplarVenta = new EjemplarVentaDTO();
        ejemplarVenta.setPrecio(ejemplarVentaDTO.getPrecio());
        ejemplarVenta.setEjemplar(ejemplarOpt.get());
        ejemplarVenta.setVenta(ejemplarVentaDTO.getVenta());*/

        EjemplarVentaModel ejemplarVentaModel = modelMapper.map(ejemplarVentaDTO, EjemplarVentaModel.class);
        ejemplarVentaModel = ejemplarVentaRepository.save(ejemplarVentaModel);

        return modelMapper.map(ejemplarVentaModel, EjemplarVentaDTO.class);
    }

    @Override
    public EjemplarVentaDTO editarEjemplarVenta(Long id, EjemplarVentaDTO ejemplarVentaDTO) {

        Optional<EjemplarVentaDTO> ejemplarVentaOpt = obtenerEjemplarVentaPorId(id);
        if(ejemplarVentaOpt.isEmpty()){
            throw new RuntimeException("EjemplarVenta no encontrado");
        }

        Optional<EjemplarDTO> ejemplarOpt = ejemplarService.obtenerEjemplarPorId(id);
        if(ejemplarOpt.isEmpty()){
            throw new RuntimeException("Ejemplar no encontrado");
        }

        EjemplarVentaDTO ejemplarVenta = ejemplarVentaOpt.get();
        ejemplarVenta.setPrecio(ejemplarVentaDTO.getPrecio());
        ejemplarVenta.setEjemplar(ejemplarOpt.get());
        //ejemplarVenta.setVenta(ejemplarVentaDTO.getVenta());

        EjemplarVentaModel ejemplarVentaModel = modelMapper.map(ejemplarVenta, EjemplarVentaModel.class);
        ejemplarVentaModel = ejemplarVentaRepository.save(ejemplarVentaModel);

        return modelMapper.map(ejemplarVentaModel, EjemplarVentaDTO.class);
    }

    @Override
    public Optional<EjemplarVentaDTO> obtenerEjemplarVentaPorId(Long id) {
        return ejemplarVentaRepository.findById(id)
            .map(model -> modelMapper.map(model, EjemplarVentaDTO.class));
    }
    
}
