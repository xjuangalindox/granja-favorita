package com.example.demo.services;

import java.lang.foreign.Linker.Option;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.repositories.EjemplarRepository;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;
import com.example.demo.util.ArchivoUtil;

@Service
public class NacimientoServiceImpl implements INacimientoService{

	private static final String RUTA_EJEMPLARES = "src/main/resources/static/img/ejemplares";

    @Autowired
    private NacimientoRepository nacimientoRepository;

    @Autowired
    private MontaRepository montaRepository;

    @Autowired
    private IEjemplarService ejemplarService;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<NacimientoDTO> obtenerNacimientos() {
        List<NacimientoModel> entitiesList = (List<NacimientoModel>) nacimientoRepository.findAll();
        entitiesList.sort(Comparator.comparing(item -> item.getFechaNacimiento()));
        
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
    public Optional<NacimientoDTO> obtenerNacimientoById(Long id) {
        return nacimientoRepository.findById(id)
            .map(model -> modelMapper.map(model, NacimientoDTO.class));
    }

    @Override
    public NacimientoDTO guardarNacimiento(NacimientoDTO nacimientoDTO) {

        // Buscar MontaModel del NacimientoModel
        MontaModel montaModel = montaRepository.findById(nacimientoDTO.getMonta().getId())
            .orElseThrow(() -> new RuntimeException("Monta no encontrada"));

		// Mapear y persistir NacimientoModel
		NacimientoModel nacimientoModel = new NacimientoModel();
		nacimientoModel.setFechaNacimiento(nacimientoDTO.getFechaNacimiento());
		nacimientoModel.setGazaposVivos(nacimientoDTO.getGazaposVivos());
		nacimientoModel.setGazaposMuertos(nacimientoDTO.getGazaposMuertos());
		nacimientoModel.setNota(nacimientoDTO.getNota());
        nacimientoModel.setMonta(montaModel);

        nacimientoModel = nacimientoRepository.save(nacimientoModel);

        // Filtar ejemplares validos
		if(nacimientoDTO.getEjemplares() != null && !nacimientoDTO.getEjemplares().isEmpty()){
            nacimientoDTO.setEjemplares(filtrarEjemplaresNuevos(nacimientoDTO.getEjemplares()));
        
            // ¿Existe algun ejemplar valido?
            if(!nacimientoDTO.getEjemplares().isEmpty()){
                List<EjemplarModel> ejemplares = new ArrayList<>();

                String nombreConejo = montaModel.getMacho().getNombre();
                String nombreConeja = montaModel.getHembra().getNombre();
                LocalDate fechaNacimiento = nacimientoDTO.getFechaNacimiento();
                String nombreBase = nombreConejo +"_"+ nombreConeja +"_"+ fechaNacimiento;

                for(EjemplarDTO item : nacimientoDTO.getEjemplares()){
                    // Guardar imagen en disco
                    String nombreImagen = null;
                    if(item.getImagen() != null && !item.getImagen().isEmpty()){
                        nombreImagen = guardarImagenEnDisco(nombreBase, item.getImagen());
                    }

                System.out.println("\n\n\n");
                System.out.println("SERVICE");
                System.out.println("NOMBRE IMAGEN: "+nombreImagen);
                System.out.println("\n\n\n");

                    // Mapear y persitir EjemplarModel
                    EjemplarModel ejemplarModel = new EjemplarModel();
                    ejemplarModel.setNombreImagen(nombreImagen);
                    ejemplarModel.setSexo(item.getSexo());
                    ejemplarModel.setDisponible(item.isDisponible());
                    ejemplarModel.setNacimiento(nacimientoModel);

                    // Pesistir y agregar a la lista de ejemplares (model)
                    ejemplarModel = ejemplarRepository.save(ejemplarModel);
                    ejemplares.add(ejemplarModel);
                }

                // Agregar lista de ejempalres (model) a NacimientoModel y persistir
                nacimientoModel.setEjemplares(ejemplares);
                nacimientoModel = nacimientoRepository.save(nacimientoModel);
            }
		}

        // Mapear y retornar
        return modelMapper.map(nacimientoModel, NacimientoDTO.class);
    }

    private List<EjemplarDTO> filtrarEjemplaresNuevos(List<EjemplarDTO> ejemplares) {
        return ejemplares.stream()
            .filter(item -> 
                (item.getImagen() != null && !item.getImagen().isEmpty()) &&
                item.getSexo() != null &&
                item.isDisponible())
            .collect(Collectors.toList());
    }

    private String guardarImagenEnDisco(String nombreBase, MultipartFile imagen) {
        String extension = ArchivoUtil.obtenerExtensionImagen(imagen);
        String nombreImagen = ArchivoUtil.crearNombreImagen(nombreBase, extension);
        Path ruta = ArchivoUtil.crearRuta(RUTA_EJEMPLARES, nombreImagen);

        int contador = 1;
        while (Files.exists(ruta)) {
            nombreImagen = nombreBase + "(" + contador + ")" + extension;
            ruta = ArchivoUtil.crearRuta(RUTA_EJEMPLARES, nombreImagen);
            contador++;
        }

        try {
            Files.write(ruta, imagen.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen");
        }

        return nombreImagen;
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
        Optional<NacimientoDTO> nacimientoOpt = obtenerNacimientoById(id);
        if(nacimientoOpt.isPresent()){
            NacimientoDTO nacimientoDTO = nacimientoOpt.get();
            
            // Eliminar ejemplares con sus imagenes
            if(nacimientoDTO.getEjemplares() != null && !nacimientoDTO.getEjemplares().isEmpty()){
                for(EjemplarDTO item : nacimientoDTO.getEjemplares()){
                    Path ruta = ArchivoUtil.crearRuta(RUTA_EJEMPLARES, item.getNombreImagen());

                    try {
                        Files.deleteIfExists(ruta); // Eliminar imagen
                        ejemplarService.eliminarEjemplarPorId(item.getId()); //Eliminar ejemplar

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Eliminar nacimiento
            nacimientoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        return nacimientoRepository.existsById(id);
    }   
}
