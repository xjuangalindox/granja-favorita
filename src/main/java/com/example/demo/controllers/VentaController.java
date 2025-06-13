package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.controllers.dto.EjemplarVentaDTO;
import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.services.IArticuloService;
import com.example.demo.services.IArticuloVentaService;
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
    
    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IArticuloService articuloService;

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
        model.addAttribute("listaNacimientos", nacimientoService.obtenerNacimientos());

        return "ventas/formulario";
    }

    @PostMapping("/ventas/guardar")
    public String guardarVenta(@ModelAttribute("ventaDTO") VentaDTO ventaDTO, Model model, RedirectAttributes redirectAttributes) {
        VentaDTO venta;
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
        try {
            venta = ventaService.guardarVenta(ventaDTO);

            if(articulosVenta != null && !articulosVenta.isEmpty()){
                articulosVenta.forEach(articuloVenta -> articuloVenta.setVenta(venta));
                articulosVenta.forEach(articuloVenta -> articuloVentaService.guardarArticuloVenta(articuloVenta));
            }

            if(ejemplaresVenta != null && !ejemplaresVenta.isEmpty()){
                ejemplaresVenta.forEach(ejemplarVenta -> ejemplarVenta.setVenta(venta));
                ejemplaresVenta.forEach(ejemplarVenta -> ejemplarVentaService.guardarEjemplarVenta(ejemplarVenta));
            }

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Registrar Venta");
            model.addAttribute("accion", "/ventas/guardar");
            model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
            model.addAttribute("listaNacimientos", nacimientoService.obtenerNacimientos());

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
        Set<Long> idNacimientos = new HashSet<>();

        // Obtener idNacimiento de cada EjemplarVenta (sin duplicados)
        if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
            for(EjemplarVentaDTO eje : ventaDTO.getEjemplaresVenta()){
                idNacimientos.add(eje.getEjemplar().getNacimiento().getId());
            }
        }

        model.addAttribute("ventaDTO", ventaDTO);
        model.addAttribute("titulo", "Editar Venta");
        model.addAttribute("accion", "/ventas/editar/"+id);
        model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
        model.addAttribute("listaNacimientos", nacimientoService.obtenerNacimientos());
        model.addAttribute("idNacimientos", idNacimientos);

        return "ventas/formulario";
    }

    @PostMapping("/ventas/editar/{id}")
    public String editarVenta(@PathVariable("id") Long id, @ModelAttribute("ventaDTO") VentaDTO ventaDTO,
        @RequestParam(name = "articulosVentaEliminados", required = false) List<Long> articulosVentaEliminados,
        @RequestParam(name = "ejemplaresVentaEliminados", required = false) List<Long> ejemplaresVentaEliminados,
        Model model, RedirectAttributes redirectAttributes) {

        System.out.println("\n\n\n");
        System.out.println("\n\n\n");
        System.out.println(ventaDTO.toString());
        System.out.println("\n\n\n");
        System.out.println("\n\n\n");

        // Acceso desde la barra de direcciones
        /*Optional<VentaDTO> ventaOpt = ventaService.obtenerVentaPorId(id);
        if(ventaOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Venta no encontrada.");
            return "redirect:/ventas";
        }

        // 1. Eliminar articulos y ejemplares eliminados en el formulario
        if(articulosVentaEliminados != null && !articulosVentaEliminados.isEmpty()){
            articulosVentaEliminados.forEach(item -> articuloVentaService.eliminarArticuloVentaPorId(item));
        }
        if(ejemplaresVentaEliminados != null && !ejemplaresVentaEliminados.isEmpty()){
            ejemplaresVentaEliminados.forEach(item -> ejemplarVentaService.eliminarEjemplarVentaPorId(item));
        }

        // Listas para articulosVenta y ejemplaresVenta filtrados (nuevos y existentes)
        VentaDTO venta;
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

        // 3. Persistir por separado: venta, articulosVenta y ejemplaresVenta (nuevos y existentes)
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
                ejemplaresExistentes.forEach(ejemplarVenta -> ejemplarVentaService.editarEjemplarVenta(ejemplarVenta.getId(), ejemplarVenta));
            }

        } catch (Exception e) {
            model.addAttribute("ventaDTO", ventaDTO);
            model.addAttribute("titulo", "Editar Venta");
            model.addAttribute("accion", "/ventas/editar/"+id);
            model.addAttribute("listaArticulos", articuloService.obtenerArticulos());
            model.addAttribute("listaNacimientos", nacimientoService.obtenerNacimientos());

            model.addAttribute("mensaje", "Ocurrio un error al editar la venta.");
            return "ventas/formulario";
        }*/

        redirectAttributes.addFlashAttribute("ok", "Venta modificada correctamente.");
        return "redirect:/ventas";
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// DELETE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @GetMapping("/ventas/eliminar/{id}")
    public String eliminarVenta(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        ventaService.eliminarVenta(id);

        redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada correctamente");
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
    /// FILTROS (NUEVOS Y EXISTENTES)
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
                ejeVenta.getEjemplar().isVendido() == true)
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresExistentes(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(item -> item.getId() != null &&
                item.getPrecio() != null &&
                item.getEjemplar() != null &&
                item.getEjemplar().getId() != null)
            .collect(Collectors.toList());
    }
}
