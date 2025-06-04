package com.example.demo.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<EjemplarDTO> filtrarEjemplaresNuevos(List<EjemplarDTO> ejemplares) {
        return ejemplares.stream()
            .filter(item -> 
                item.getId() == null &&
                (item.getImagen() != null && !item.getImagen().isEmpty()))
            .collect(Collectors.toList());
    }

    private List<EjemplarDTO> filtrarEjemplaresExistentes(List<EjemplarDTO> ejemplares) {
        return ejemplares.stream()
            .filter(item -> 
                item.getId() != null)
            .collect(Collectors.toList());
    }

    private String guardarImagenEnDisco(String nombreBase, MultipartFile imagen) {
        String extension = ArchivoUtil.obtenerExtensionImagen(imagen);
        String nombreImagen = ArchivoUtil.crearNombreImagen(nombreBase, extension);
        Path ruta = ArchivoUtil.crearRuta(RUTA_EJEMPLARES, nombreImagen);

        int contador = 1;
        while (Files.exists(ruta)) {
            nombreImagen = ArchivoUtil.crearNombreImagen(nombreBase + "(" + contador + ")", extension);
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public NacimientoDTO editarNacimiento(Long id, NacimientoDTO nacimientoDTO, List<Long> ejemplaresEliminados) {
        // 1. Eliminar ejemplares eliminados en el formulario
        if(ejemplaresEliminados != null && !ejemplaresEliminados.isEmpty()){

            ejemplaresEliminados.forEach(item -> {
                Optional<EjemplarModel> ejemplarOpt = ejemplarRepository.findById(item);
                if(ejemplarOpt.isPresent()){
                    EjemplarModel ejemplarModel = ejemplarOpt.get();
                    Path ruta = ArchivoUtil.crearRuta(RUTA_EJEMPLARES, ejemplarModel.getNombreImagen());

                    try {
                        // Desvincular ejemplar de nacimiento y eliminar
                        ejemplarModel.setNacimiento(null);
                        ejemplarModel = ejemplarRepository.save(ejemplarModel);
                        ejemplarRepository.deleteById(ejemplarModel.getId());
                        // Eliminar imagen en disco del ejemplar
                        Files.deleteIfExists(ruta);

                    } catch (Exception e) {
                        throw new RuntimeException("Ocurrió un error al eliminar el ejemplar");
                    }
                }
            });
        }

        // 2. Settear informacion y actualizar el nacimiento
        NacimientoModel nacimientoModel = nacimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Nacimiento no encontrado"));

        MontaModel montaModel = montaRepository.findById(nacimientoDTO.getMonta().getId())
            .orElseThrow(() -> new RuntimeException("Monta no encontrada"));

        nacimientoModel.setFechaNacimiento(nacimientoDTO.getFechaNacimiento());
        nacimientoModel.setGazaposVivos(nacimientoDTO.getGazaposVivos());
        nacimientoModel.setGazaposMuertos(nacimientoDTO.getGazaposMuertos());
        nacimientoModel.setNota(nacimientoDTO.getNota());
        nacimientoModel.setMonta(montaModel);

        nacimientoModel = nacimientoRepository.save(nacimientoModel);

        // 3. ¿El nacimiento tiene ejemplares?
        if(nacimientoDTO.getEjemplares() != null && !nacimientoDTO.getEjemplares().isEmpty()){
            // Filtrar ejemplares validos (nuevos y modificados)    
            List<EjemplarDTO> ejemplaresNuevos = filtrarEjemplaresNuevos(nacimientoDTO.getEjemplares());
            List<EjemplarDTO> ejemplaresExistentes = filtrarEjemplaresExistentes(nacimientoDTO.getEjemplares());
            
            // Nombre base para imagenes
            String nombreConejo = montaModel.getMacho().getNombre();
            String nombreConeja = montaModel.getHembra().getNombre();
            LocalDate fechaNacimiento = nacimientoModel.getFechaNacimiento();
            String nombreBase = nombreConejo +"_"+ nombreConeja +"_"+ fechaNacimiento;

            // 4. Persistir ejemplares nuevos y agregarlos a la lista
            if(ejemplaresNuevos != null && !ejemplaresNuevos.isEmpty()){
                for(EjemplarDTO item : ejemplaresNuevos){
                    String nombreImagen = null;
                    if(item.getImagen() != null && !item.getImagen().isEmpty()){
                        nombreImagen = guardarImagenEnDisco(nombreBase, item.getImagen());
                    }

                    EjemplarModel ejemplarModel = new EjemplarModel();
                    ejemplarModel.setNombreImagen(nombreImagen);
                    ejemplarModel.setSexo(item.getSexo());
                    ejemplarModel.setDisponible(item.isDisponible());
                    ejemplarModel.setNacimiento(nacimientoModel);

                    ejemplarRepository.save(ejemplarModel);
                }
            }

            // 5. Persistir ejemplares existentes y agregarlos a la lista
            if(ejemplaresExistentes != null && !ejemplaresExistentes.isEmpty()){
                for(EjemplarDTO item : ejemplaresExistentes){

                    EjemplarModel ejemplarModel = ejemplarRepository.findById(item.getId())
                        .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

                    Path ruta = null;
                    String nombreImagen = null;
                    if(item.getImagen() != null && !item.getImagen().isEmpty()){
                        ruta = ArchivoUtil.crearRuta(RUTA_EJEMPLARES, item.getNombreImagen());
                        
                        try {
                            Files.deleteIfExists(ruta);
                            nombreImagen = guardarImagenEnDisco(nombreBase, item.getImagen());

                        } catch (Exception e) {
                            throw new RuntimeException("Error al modificar el ejemplar");
                        }
                    }

                    if(nombreImagen != null){
                        ejemplarModel.setNombreImagen(nombreImagen);
                    }
                    ejemplarModel.setSexo(item.getSexo());
                    ejemplarModel.setDisponible(item.isDisponible());
                    ejemplarModel.setNacimiento(nacimientoModel);

                    ejemplarRepository.save(ejemplarModel);
                }
            }
        }

        // 6. Mapear y retornar
        return modelMapper.map(nacimientoModel, NacimientoDTO.class);
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
