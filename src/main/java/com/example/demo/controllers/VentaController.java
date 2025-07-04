package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.EjemplarVentaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.models.EjemplarVentaModel;
import com.example.demo.repositories.ArticuloVentaRepository;
import com.example.demo.repositories.EjemplarVentaRepository;
import com.example.demo.services.IArticuloService;
import com.example.demo.services.IArticuloVentaService;
import com.example.demo.services.IEjemplarService;
import com.example.demo.services.IEjemplarVentaService;
import com.example.demo.services.INacimientoService;
import com.example.demo.services.IVentaService;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VentaController {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VentaController.class);
    
    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IArticuloService articuloService;
    
    @Autowired
    private IEjemplarService ejemplarService;

    @Autowired
    private INacimientoService nacimientoService;

    @Autowired
    private IArticuloVentaService articuloVentaService;

    @Autowired
    private IEjemplarVentaService ejemplarVentaService;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// SELECT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/ventas")
    public String obtenerVentas(Model model) {
        List<VentaDTO> listaVentas = ventaService.obtenerVentas();
        model.addAttribute("listaVentas", listaVentas);

        return "ventas/lista";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// INSERT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/ventas/crear")
    public String formularioCrear(Model model) {
        model.addAttribute("ventaDTO", new VentaDTO());
        model.addAttribute("titulo", "Registrar Venta");
        model.addAttribute("accion", "/ventas/guardar");
        model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
        model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos()));

        return "ventas/formulario";
    }

    @PostMapping("/ventas/guardar")
    public String guardarVenta(@ModelAttribute("ventaDTO") VentaDTO ventaDTO, Model model, RedirectAttributes redirectAttributes) {
        List<ArticuloVentaDTO> articulosVenta = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresVenta = new ArrayList<>();

        // 1. Filtrar articulos y ejemplares validos
        if(ventaDTO.getArticulosVenta() != null && !ventaDTO.getArticulosVenta().isEmpty()){
            articulosVenta = filtrarArticulosNuevos(ventaDTO.getArticulosVenta());
        }
        if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
            ejemplaresVenta = filtrarEjemplaresNuevos(ventaDTO.getEjemplaresVenta());
        }

        // 2. Persistir por separado: venta, articulosVenta y ejemplaresVenta (nuevos)
        VentaDTO venta;

        try {
            venta = ventaService.guardarVenta(ventaDTO);

            if(articulosVenta != null && !articulosVenta.isEmpty()){
                articulosVenta.forEach(articuloVenta -> {
                    articuloVenta.setVenta(venta);
                    articuloVentaService.guardarArticuloVenta(articuloVenta);
                });
            }

            if(ejemplaresVenta != null && !ejemplaresVenta.isEmpty()){
                ejemplaresVenta.forEach(ejemplarVenta -> {
                    ejemplarVenta.setVenta(venta);
                    ejemplarVentaService.guardarEjemplarVenta(ejemplarVenta);
                });
            }

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Registrar Venta");
            model.addAttribute("accion", "/ventas/guardar");
            model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
            model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos()));

            model.addAttribute("mensaje", "Ocurrió un error al registrar la venta.");
            return "ventas/formulario";
        }

        // 3. Mostrar venta registrada
        redirectAttributes.addFlashAttribute("ok", "Venta registrada correctamente.");
        return "redirect:/ventas";
    } 
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// UPDATE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/ventas/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        
        Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        if(ventaOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Venta no encontrada.");
            return "redirect:/ventas";
        }

        VentaDTO ventaDTO = ventaOpt.get();
        List<NacimientoDTO> listaNacimientos = filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos());
        listaNacimientos = fusionarEjemplaresVendidosConEjemplaresDisponibles(ventaDTO.getEjemplaresVenta(), listaNacimientos);

        model.addAttribute("ventaDTO", ventaDTO);
        model.addAttribute("titulo", "Editar Venta");
        model.addAttribute("accion", "/ventas/editar/"+id);
        
        model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
        model.addAttribute("listaNacimientos", listaNacimientos); // nacimientos con ejemplares vendidos y disponibles
        model.addAttribute("idsNacimientosUtilizados", getIdsNacimientosUtilizados(ventaDTO.getEjemplaresVenta()));

        return "ventas/formulario";
    }

    @Autowired
    private ArticuloVentaRepository articuloVentaRepository;

    @Autowired
    private EjemplarVentaRepository ejemplarVentaRepository;

    @PostMapping("/ventas/editar/{id}")
    public String editarVenta(@PathVariable("id") Long id, @ModelAttribute("ventaDTO") VentaDTO ventaDTO,
        @RequestParam(name = "articulosEliminados", required = false) List<Long> idsArticulosVentaEliminados,
        @RequestParam(name = "nacimientosEliminados", required = false) List<Long> idsNacimientosEliminados,
        Model model, RedirectAttributes redirectAttributes) {

        // Obtener ventaDTO original
        Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        VentaDTO ventaOriginal = ventaOpt.get();

        System.out.println("\n\n");
        System.out.println("\n\n");
        System.out.println("ventaDTO - Formulario");
        System.out.println(ventaDTO);
        System.out.println("\n");
        System.out.println("idsNacimientosEliminados: "+idsNacimientosEliminados);
        System.out.println("idsArticulosVentaEliminados: "+idsArticulosVentaEliminados);
        System.out.println("\n\n");
        System.out.println("\n\n");

        // 1. Eliminar articulos venta eliminados en el formulario
        if(idsArticulosVentaEliminados != null && !idsArticulosVentaEliminados.isEmpty()){
            try {
                // Por cada articulo venta eliminado en el formulario
                idsArticulosVentaEliminados.forEach(idArtVenta -> {
                    // Obtener model de articulo venta
                    Optional<ArticuloVentaModel> articuloVentaModelOpt = articuloVentaRepository.findById(idArtVenta);
                    ArticuloVentaModel articuloVentaModel = articuloVentaModelOpt.get();

                    // Desvincular articulo venta de la venta
                    articuloVentaModel.setVenta(null);
                    articuloVentaRepository.save(articuloVentaModel);

                    // Eliminar articulo venta
                    articuloVentaRepository.deleteById(articuloVentaModel.getId());
                });

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Ocurrió un error al eliminar el articulo venta");
                return "redirect:/ventas";
            }
            
        }

        // 2. Liberar ejemplares y eliminar ejemplares venta eliminados en el formulario
        if(idsNacimientosEliminados != null && !idsNacimientosEliminados.isEmpty()){
            for(EjemplarVentaDTO ejemplarVentaDTO : ventaOriginal.getEjemplaresVenta()){
                EjemplarDTO ejemplarDTO = ejemplarVentaDTO.getEjemplar();

                if(idsNacimientosEliminados.contains(ejemplarDTO.getNacimiento().getId())){
                    try {
                        // Liberar ejemplar
                        ejemplarDTO.setVendido(false);
                        ejemplarService.editarEjemplar(ejemplarDTO);
                        
                        // Obtener model de ejemplar venta
                        Optional<EjemplarVentaModel> ejemplarVentaModelOpt = ejemplarVentaRepository.findById(ejemplarVentaDTO.getId());
                        EjemplarVentaModel ejemplarVentaModel = ejemplarVentaModelOpt.get();
                        
                        // Desvincular ejemplar venta de la venta
                        ejemplarVentaModel.setVenta(null);
                        ejemplarVentaRepository.save(ejemplarVentaModel);

                        // Eliminar ejemplar venta
                        ejemplarVentaRepository.deleteById(ejemplarVentaModel.getId());

                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("error", "Ocurrió un error al liberar el ejemplar y eliminar el ejemplar venta");
                        return "redirect:/ventas";
                    }
                }
            }
        }

        // Listas para articulosVenta y ejemplaresVenta filtrados (nuevos y existentes)
        List<ArticuloVentaDTO> articulosNuevos = new ArrayList<>();
        List<ArticuloVentaDTO> articulosExistentes = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresNuevos = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresExistentes = new ArrayList<>();

        // 2. Filtrar articulosVenta y ejemplaresVenta (nuevos y existentes)
        if(ventaDTO.getArticulosVenta() != null && !ventaDTO.getArticulosVenta().isEmpty()){
            articulosNuevos = filtrarArticulosNuevos(ventaDTO.getArticulosVenta());
            articulosExistentes = filtrarArticulosExistentes(ventaDTO.getArticulosVenta());
        }
        if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
            ejemplaresNuevos = filtrarEjemplaresNuevos(ventaDTO.getEjemplaresVenta());
            ejemplaresExistentes = filtrarEjemplaresExistentes(ventaDTO.getEjemplaresVenta());
        }

        System.out.println("\n\n");
        System.out.println("articulos nuevos: "+articulosNuevos);
        System.out.println("\n");
        System.out.println("articulos modificados: "+articulosExistentes);
        System.out.println("\n");
        System.out.println("ejemplares nuevos: "+ejemplaresNuevos);
        System.out.println("\n");
        System.out.println("ejemplares modificados: "+ejemplaresExistentes);
        System.out.println("\n\n");

        // 3. Persistir por separado: venta, articulosVenta y ejemplaresVenta (nuevos y existentes)
        VentaDTO venta;
        
        try {
            venta = ventaService.editarVenta(id, ventaDTO);

            if(articulosNuevos != null && !articulosNuevos.isEmpty()){
                articulosNuevos.forEach(articuloVenta -> {
                    articuloVenta.setVenta(venta);
                    articuloVentaService.guardarArticuloVenta(articuloVenta);
                });
            }
            if(ejemplaresNuevos != null && !ejemplaresNuevos.isEmpty()){
                ejemplaresNuevos.forEach(ejemplarVenta -> {
                    ejemplarVenta.setVenta(venta);
                    ejemplarVentaService.guardarEjemplarVenta(ejemplarVenta);
                });
            }

            if(articulosExistentes != null && !articulosExistentes.isEmpty()){
                articulosExistentes.forEach(articuloVenta -> articuloVentaService.editarArticuloVenta(articuloVenta.getId(), articuloVenta));
            }
            if(ejemplaresExistentes != null && !ejemplaresExistentes.isEmpty()){
                for(EjemplarVentaDTO ejemplarVentaDTO : ejemplaresExistentes){
                    // Ejemplar sigue vendido
                    if(ejemplarVentaDTO.getEjemplar().isVendido()){
                        ejemplarVentaService.editarEjemplarVenta(ejemplarVentaDTO.getId(), ejemplarVentaDTO);

                    // Ejemplar paso a disponible
                    }else{
                        try {
                            // 1. Liberar ejemplar
                            // Obtener ejemplar
                            Optional<EjemplarDTO> ejemplarOpt = ejemplarService.obtenerEjemplarPorId(ejemplarVentaDTO.getEjemplar().getId());
                            EjemplarDTO ejemplarDTO = ejemplarOpt.get();

                            // Liberar ejemplar
                            ejemplarDTO.setVendido(false);
                            ejemplarService.editarEjemplar(ejemplarDTO);

                            // 2. Eliminar ejemplar venta
                            // Obtener ejemplar venta
                            Optional<EjemplarVentaModel> ejemplarVentaModelOpt = ejemplarVentaRepository.findById(ejemplarVentaDTO.getId());
                            EjemplarVentaModel ejemplarVentaModel = ejemplarVentaModelOpt.get();

                            // Desvincular ejemplar venta de venta
                            ejemplarVentaModel.setVenta(null);
                            ejemplarVentaRepository.save(ejemplarVentaModel);

                            // Eliminar ejemplar venta
                            ejemplarVentaRepository.deleteById(ejemplarVentaModel.getId());

                        } catch (Exception e) {
                            // Revisar porque se ejecuta esta exception
                            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al liberar el ejemplar o eliminar el ejemplar venta");
                            return "redirect:/ventas";
                        }
                    }
                }
            }

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Editar Venta");
            model.addAttribute("accion", "/ventas/editar/"+id);
            
            model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
            model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(nacimientoService.obtenerNacimientos()));
            model.addAttribute("idsNacimientosUtilizados", getIdsNacimientosUtilizados(ventaDTO.getEjemplaresVenta()));

            model.addAttribute("mensaje", "Ocurrio un error al editar la venta.");
            return "ventas/formulario";
        }

        redirectAttributes.addFlashAttribute("ok", "Venta modificada correctamente.");
        return "redirect:/ventas";
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DELETE - READY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @GetMapping("/ventas/eliminar/{id}")
    public String eliminarVenta(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        if(ventaOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Venta no encontrada.");
            return "redirect:/ventas";
        }

        try {
            ventaService.eliminarVenta(ventaOpt.get());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al eliminar la venta.");
            return "redirect:/ventas";
        }

        redirectAttributes.addFlashAttribute("ok", "Venta eliminada correctamente.");
        return "redirect:/ventas";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ENDPOINT PRUEBA
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/prueba/{id}")
    public ResponseEntity<VentaDTO> metodoPrueba(@PathVariable("id") Long id){
        Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        if(ventaOpt.isPresent()){
            return ResponseEntity.ok(ventaOpt.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FILTROS (NUEVOS Y EXISTENTES) Y EJEMPLARES DISPONIBLES (EN NACIMIENTO)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ArticuloVentaDTO> filtrarArticulosNuevos(List<ArticuloVentaDTO> articulosVenta){
        return articulosVenta.stream()
            .filter(item -> 
                item.getId() == null &&
                item.getCantidad() != null &&
                item.getSubtotal() != null &&
                item.getArticulo() != null &&
                item.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    private List<ArticuloVentaDTO> filtrarArticulosExistentes(List<ArticuloVentaDTO> articulosVenta) {
        return articulosVenta.stream()
            .filter(item -> item.getId() != null &&
                item.getCantidad() != null &&
                item.getSubtotal() != null &&
                item.getArticulo() != null &&
                item.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresNuevos(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(ejeVenta -> 
                ejeVenta.getId() == null &&
                ejeVenta.getPrecio() != null &&
                ejeVenta.getEjemplar() != null &&
                ejeVenta.getEjemplar().getId() != null &&
                ejeVenta.getEjemplar().isVendido())
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresExistentes(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(item -> 
                item.getId() != null &&
                //item.getPrecio() != null &&
                item.getEjemplar() != null &&
                item.getEjemplar().getId() != null)
            .collect(Collectors.toList());
    }

    // Filtrar lista de nacimientos con ejemplares disponibles, 
    private List<NacimientoDTO> filtrarNacimientosConEjemplaresDisponibles(List<NacimientoDTO> listaNacimientos){
        if(listaNacimientos == null || listaNacimientos.isEmpty()){
            return listaNacimientos;
        }

        return listaNacimientos.stream()
            .map(nac -> {
                List<EjemplarDTO> disponibles = nac.getEjemplares().stream()
                    .filter(eje -> !eje.isVendido())
                    .collect(Collectors.toList());
                    
                nac.setEjemplares(disponibles);
                return nac;
            })
            .filter(nac -> nac.getEjemplares() != null && !nac.getEjemplares().isEmpty())
            .collect(Collectors.toList());
    }

    // Obtener idNacimiento de cada EjemplarVenta (sin duplicados)
    private Set<Long> getIdsNacimientosUtilizados(List<EjemplarVentaDTO> ejemplaresVenta){
        Set<Long> idsNacimientosUnicos = new HashSet<>();

        if(ejemplaresVenta != null && !ejemplaresVenta.isEmpty()){
            ejemplaresVenta.forEach(eje -> idsNacimientosUnicos.add(eje.getEjemplar().getNacimiento().getId()));
        }
        
        return idsNacimientosUnicos;
    }

    // Agregar ejemplar vendidos a la lista de nacimientos
    private List<NacimientoDTO> fusionarEjemplaresVendidosConEjemplaresDisponibles(List<EjemplarVentaDTO> vendidos, List<NacimientoDTO> listaNacimientos){
        vendidos.forEach(ejemplarVenta -> {
            Long idNacimiento = ejemplarVenta.getEjemplar().getNacimiento().getId();
            boolean encontrado = false;

            for(NacimientoDTO nac : listaNacimientos){
                if(nac.getId().equals(idNacimiento)){
                    // Agregar ejemplar vendido al nacimiento
                    nac.getEjemplares().add(ejemplarVenta.getEjemplar());
                    encontrado = true;
                    break;
                }
            }

            if(!encontrado){
                // Obtener nacimiento de la base de datos
                Optional<NacimientoDTO> nacimientoOpt = nacimientoService.obtenerNacimientoById(idNacimiento);
                if(nacimientoOpt.isPresent()){
                    NacimientoDTO nacimientoDTO = nacimientoOpt.get();
                    // Limpiar lista de ejemplares y agregar ejemplar vendido
                    nacimientoDTO.setEjemplares(new ArrayList<>());
                    nacimientoDTO.getEjemplares().add(ejemplarVenta.getEjemplar());
                    // Agregar nacimiento a la lista de nacimientos
                    listaNacimientos.add(nacimientoDTO);
                }
            }
        });

        return listaNacimientos;
    }
}
