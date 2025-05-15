package com.example.demo.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.services.IArticuloService;
import com.example.demo.services.IArticuloVentaService;
import com.example.demo.services.IEjemplarService;
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
    
    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IArticuloService articuloService;

    @Autowired
    private INacimientoService nacimientoService;

    @Autowired
    private IArticuloVentaService articuloVentaService;

    @Autowired
    private IEjemplarService ejemplarService;

    @GetMapping("/ventas")
    public String obtenerVentas(Model model) {
        List<VentaDTO> listaVentas = ventaService.obtenerVentas();
        model.addAttribute("listaVentas", listaVentas);

        return "ventas/lista";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/ventas/crear")
    public String formularioCrear(Model model) {
        model.addAttribute("ventaDTO", new VentaDTO());
        model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
        model.addAttribute("listaNacimientos", nacimientoService.obtenerNacimientos());
        model.addAttribute("titulo", "Registrar Venta");
        model.addAttribute("accion", "/ventas/guardar");

        return "ventas/formulario";
    }

    @PostMapping("/ventas/guardar")
    public String guardarVenta(@ModelAttribute("ventaDTO") VentaDTO ventaDTO, RedirectAttributes redirectAttributes) {
        //Filtrar nuevos articulos y ejemplares
        List<ArticuloVentaDTO> articulos = this.filtrarArticulosValidos(ventaDTO.getArticulos());
        List<EjemplarDTO> ejemplares = this.filtrarEjemplaresValidos(ventaDTO.getEjemplares());

        //Setter articulos y ejemplares a VentaDTO
        ventaDTO.setArticulos(articulos);
        ventaDTO.setEjemplares(ejemplares);

        ventaService.guardarVenta(ventaDTO);
        redirectAttributes.addFlashAttribute("mensaje", "Venta registrada correctamente");
        return "redirect:/ventas";
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/ventas/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id, Model model) {
        VentaDTO ventaDTO = ventaService.obtenerVentaPorId(id);
        
        model.addAttribute("ventaDTO", ventaDTO);
        model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
        model.addAttribute("listaNacimientos", nacimientoService.obtenerNacimientos());
        model.addAttribute("titulo", "Editar Venta");
        model.addAttribute("accion", "/ventas/editar/"+id);

        return "ventas/formulario";
    }

    @PostMapping("/ventas/editar/{id}")
    public String editarVenta(
        @PathVariable("id") Long id, 
        @ModelAttribute("ventaDTO") VentaDTO ventaDTO,
        @RequestParam(name = "articulosEliminados", required = false) List<Long> articulosEliminados,
        @RequestParam(name = "ejemplaresEliminados", required = false) List<Long> ejemplaresEliminados,
        RedirectAttributes redirectAttributes
        ) {

        // 1. Elimiar articulos y ejemplares eliminados en el formulario
        if(articulosEliminados != null){
            for(Long item : articulosEliminados){
            articuloVentaService.eliminarArticuloVentaPorId(item);
            }
        }
        if(ejemplaresEliminados != null){
            for(Long item : ejemplaresEliminados){
                ejemplarService.eliminarEjemplarPorId(item);
            }
        }

        // 3. Filtrar articulos y ejemplares validos (eliminar objetos vacios)
        List<ArticuloVentaDTO> listaArticulos = this.filtrarArticulosValidos(ventaDTO.getArticulos());
        List<EjemplarDTO> listaEjemplares = this.filtrarEjemplaresValidos(ventaDTO.getEjemplares());

        // 4. Modificar la img del ejemplar (si existe)
        for(EjemplarDTO item : listaEjemplares){         

            // Caso 1: Ejemplar nuevo (id == null), imagen obligatoria
            if(item.getId() == null && item.getImagen() != null && !item.getImagen().isEmpty()){
                String nombreImagen = item.getImagen().getOriginalFilename();
                Path ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);

                // Nombre sin extension de img
                String nombreSinExtension = FilenameUtils.getBaseName(nombreImagen); // Ejemplo: "conejoNegro"
                // Extension de img
                String extension = "." + FilenameUtils.getExtension(nombreImagen); // Ejemplo: ".jpg"

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

            // Caso 2: Ejemplar existente (id != null), imagen nueva opcional
            if(item.getId() != null && item.getImagen() != null && !item.getImagen().isEmpty()){                
                //Se modifico la img del ejemplar en el formulario
                String nombreImagen = item.getImagen().getOriginalFilename();
                Path ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);
                Path rutaAntigua = Paths.get("src/main/resources/static/img/ejemplares", item.getNombreImagen());

                // Nombre sin extension de img
                String nombreSinExtension = FilenameUtils.getBaseName(nombreImagen); // Ejemplo: "conejoNegro"
                // Extension de img
                String extension = "." + FilenameUtils.getExtension(nombreImagen); // Ejemplo: ".jpg"

                int contador = 1;
                while (Files.exists(ruta)) {
                    nombreImagen = nombreSinExtension + "(" + contador + ")" + extension; // ejemplo: "conejo(1).jpg"
                    ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);
                    contador++;
                }

                try {
                    Files.write(ruta, item.getImagen().getBytes());
                    Files.deleteIfExists(rutaAntigua);
                    item.setNombreImagen(nombreImagen);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Caso 3: Ejemplar existente pero no se cargó una nueva imagen → no hacer nada
        }

        // 5. Actualizar la informacion de la venta
        ventaService.actualizarDatosPrincipales(id, ventaDTO);

        // 6. Procesar articulos nuevos y existentes
        for(ArticuloVentaDTO art : listaArticulos){
            if(art.getId() != null){
                // Articulo existente -> Actualizar
                articuloVentaService.editarArticuloVenta(art);
            }else{
                // Articulo nuevo, guardar con referencia a la venta
                articuloVentaService.agregarArticuloVenta(art, id);
            }
        }

        // 7. Procesar ejemplares nuevos y existentes
        for(EjemplarDTO eje : listaEjemplares){
            if(eje.getId() != null){
                // Ejemplar existente -> Actualizar
                ejemplarService.editarEjemplar(eje);
            }else{
                // Ejemplar nuevo, guardar con referencia a la venta
                ejemplarService.agregarEjemplar(eje, id);
            }
        }

        redirectAttributes.addFlashAttribute("mensaje", "Venta modificada correctamente");
        return "redirect:/ventas";
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/prueba/{id}")
    public ResponseEntity<VentaDTO> metodoPrueba(@PathVariable("id") Long id){
        VentaDTO ventaDTO = ventaService.obtenerVentaPorId(id);
        if(ventaDTO != null){
            return ResponseEntity.ok(ventaDTO);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ENDPOINT PRUEBA
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/ventas/eliminar/{id}")
    public String eliminarVenta(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        ventaService.eliminarVenta(id);

        redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada correctamente");
        return "redirect:/ventas";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FILTRAR ARTICULOS Y EJEMPLRES VALIDOS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<ArticuloVentaDTO> filtrarArticulosValidos(List<ArticuloVentaDTO> listaArticulos){
        return listaArticulos.stream()
            .filter(articuloVenta -> 
                articuloVenta.getArticulo() != null && 
                articuloVenta.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    public List<EjemplarDTO> filtrarEjemplaresValidos(List<EjemplarDTO> listaEjemplares){
        return listaEjemplares.stream()
            .filter(ejemplar -> 
                ejemplar.getNacimiento() != null && 
                ejemplar.getNacimiento().getId() != null)
            .collect(Collectors.toList());
    }
}
