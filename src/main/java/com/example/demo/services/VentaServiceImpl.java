package com.example.demo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.EjemplarVentaDTO;
import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.VentaModel;
import com.example.demo.repositories.VentaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VentaServiceImpl implements IVentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<VentaDTO> obtenerVentas() {
        List<VentaModel> listaVentas = (List<VentaModel>) ventaRepository.findAll();
        listaVentas.sort(Comparator.comparing(venta -> venta.getFechaEntrega(), Comparator.reverseOrder()));
        //listaVentas.sort(Comparator.comparing(item -> item.getFechaEntrega()));

        return listaVentas.stream()
            .map(item -> modelMapper.map(item, VentaDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<VentaDTO> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id)
            .map(model -> modelMapper.map(model, VentaDTO.class));
    }

    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        VentaDTO venta = new VentaDTO();
        venta.setNombreCliente(ventaDTO.getNombreCliente());
        venta.setVinculoContacto(ventaDTO.getVinculoContacto());
        venta.setTelefono(ventaDTO.getTelefono());
        venta.setFechaEntrega(ventaDTO.getFechaEntrega());
        venta.setLugarEntrega(ventaDTO.getLugarEntrega());
        venta.setTotalVenta(ventaDTO.getTotalVenta());
        venta.setNota(ventaDTO.getNota());
        venta.setEstatus(ventaDTO.getEstatus());

        VentaModel ventaModel = modelMapper.map(venta, VentaModel.class);
        ventaModel = ventaRepository.save(ventaModel);

        return modelMapper.map(ventaModel, VentaDTO.class);
    }

    @Override
    public VentaDTO actualizarDatosPrincipales(Long id, VentaDTO ventaDTO) {
        return null;
    }

    @Override
    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO) {
        Optional<VentaDTO> ventaOpt = obtenerVentaPorId(id);
        if(ventaOpt.isEmpty()){
            throw new RuntimeException("Venta no encontrada.");
        }

        VentaDTO venta = ventaOpt.get();
        venta.setNombreCliente(ventaDTO.getNombreCliente());
        venta.setVinculoContacto(ventaDTO.getVinculoContacto());
        venta.setTelefono(ventaDTO.getTelefono());
        venta.setFechaEntrega(ventaDTO.getFechaEntrega());
        venta.setLugarEntrega(ventaDTO.getLugarEntrega());
        venta.setTotalVenta(ventaDTO.getTotalVenta());
        venta.setNota(ventaDTO.getNota());
        venta.setEstatus(ventaDTO.getEstatus());

        VentaModel ventaModel = modelMapper.map(venta, VentaModel.class);
        ventaModel = ventaRepository.save(ventaModel);

        return modelMapper.map(ventaModel, VentaDTO.class);
    }

    @Autowired
    private IArticuloVentaService articuloVentaService;

    @Autowired
    private IEjemplarVentaService ejemplarVentaService;

    @Autowired
    private IEjemplarService ejemplarService;

    @Override
    public boolean eliminarVenta(VentaDTO ventaDTO) {
        List<ArticuloVentaDTO> articulosVenta = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresVenta = new ArrayList<>();

        // Obtener articulos venta y ejemplares venta (si existen)
        if(ventaDTO.getArticulosVenta() != null && !ventaDTO.getArticulosVenta().isEmpty()){
            articulosVenta = ventaDTO.getArticulosVenta();
        }
        if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
            ejemplaresVenta = ventaDTO.getEjemplaresVenta();
        }

        try {
            // Eliminar articulos venta (si existen)
            if(articulosVenta != null && !articulosVenta.isEmpty()){
                articulosVenta.forEach(articuloVenta -> articuloVentaService.eliminarArticuloVentaPorId(articuloVenta.getId()));
            }
            
            // Eliminar ejemplares venta (si existen)
            if(ejemplaresVenta != null && !ejemplaresVenta.isEmpty()){
                for(EjemplarVentaDTO ejemplarVenta : ejemplaresVenta){
                    // Liberar ejemplar
                    EjemplarDTO ejemplarDTO = ejemplarVenta.getEjemplar();
                    ejemplarDTO.setVendido(false);
                    ejemplarService.editarEjemplar(ejemplarDTO);

                    // Eliminar ejemplar venta
                    ejemplarVentaService.eliminarEjemplarVentaPorId(ejemplarVenta.getId());
                }
            }

            // Eliminar venta
            ventaRepository.deleteById(ventaDTO.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

/* 

    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {

        // Guardar imgs en proyecto y settear imagen a nombreImagen en DTO
        for (EjemplarDTO item : ventaDTO.getEjemplares()) {
            if(item.getImagen() != null && !item.getImagen().isEmpty()){
                String nombreImagen = item.getImagen().getOriginalFilename();
                Path ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);

                // Nombre sin extension de img
                String nombreSinExtension = FilenameUtils.getBaseName(item.getImagen().getOriginalFilename()); // Ejemplo: "conejoNegro"
                // Extension de img
                String extension = "." + FilenameUtils.getExtension(item.getImagen().getOriginalFilename()); // Ejemplo: ".jpg"

                int contador = 1;
                while (Files.exists(ruta)) {
                    nombreImagen = nombreSinExtension + "(" + contador + ")" + extension; // ejemplo: "conejo(1).jpg"
                    ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);
                    contador++;
                }

                try {
                    Files.write(ruta, item.getImagen().getBytes());
                    item.setNombreImagen(nombreImagen);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Tranformar de VentaDTO a VentaModel
        VentaModel ventaModel = modelMapper.map(ventaDTO, VentaModel.class);

        // Asignar VentaModel a cada ArticuloVentaModel
        for (ArticuloVentaModel item : ventaModel.getArticulos()) {
            item.setVenta(ventaModel);
        }

        // Asignar VentaModel a cada EjemplarModel
        for (EjemplarModel item : ventaModel.getEjemplares()) {
            //item.setVenta(ventaModel);
        }

        // Persistir VentaModel
        VentaModel guardado = ventaRepository.save(ventaModel);

        // Mapear VentaModel a VentaDTO y retornar
        return modelMapper.map(guardado, VentaDTO.class);
    }

    @Override
    public VentaDTO actualizarDatosPrincipales(Long id, VentaDTO ventaDTO) {
        // Obtener VentaModel con ID
        VentaModel ventaModel = ventaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        // Setear informacion de VentaDTO a VentaModel
        ventaModel.setNombreCliente(ventaDTO.getNombreCliente());
        ventaModel.setVinculoContacto(ventaDTO.getVinculoContacto());
        ventaModel.setTelefono(ventaDTO.getTelefono());
        ventaModel.setFechaEntrega(ventaDTO.getFechaEntrega());
        ventaModel.setLugarEntrega(ventaDTO.getLugarEntrega());
        ventaModel.setTotalVenta(ventaDTO.getTotalVenta());
        ventaModel.setNota(ventaDTO.getNota());
        ventaModel.setEstatus(ventaDTO.getEstatus());

        // Persistir VentaModel con nueva informacion principal
        VentaModel guardado = ventaRepository.save(ventaModel);

        // Mapear VentaModel a VentaDTO y retornar
        return modelMapper.map(guardado, VentaDTO.class);
    }

    @Override
    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO) {
        return null;
    }

    @Override
    public boolean eliminarVenta(Long id) {
        Optional<VentaModel> optionalVenta = ventaRepository.findById(id);

        // Si no existe, retorna false
        if(optionalVenta.isEmpty()){
            return false;
        }

        // Obtener VentaModel de Optional
        VentaModel ventaModel = optionalVenta.get();

        for(EjemplarModel item : ventaModel.getEjemplares()){
            Path ruta = Paths.get("src/main/resources/static/img/ejemplares", item.getNombreImagen());
            try {
                Files.deleteIfExists(ruta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Eliminar la venta y todo lo asociado
        ventaRepository.deleteById(id);
        return true;
    }*/

}
