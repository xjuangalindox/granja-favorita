package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.controllers.dto.ArticuloDTO;
import com.example.demo.services.IArticuloService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArticuloController {
    
    @Autowired
    private IArticuloService articuloService;

    @GetMapping("/articulos")
    public String obtenerArticulos(Model model){
        List<ArticuloDTO> listaArticulos = articuloService.obtenerArticulos();
        model.addAttribute("listaArticulos", listaArticulos);

        return "articulos/lista";
    }

    @GetMapping("/articulos/crear")
    public String formularioCrear(Model model) {
        model.addAttribute("articuloDTO", new ArticuloDTO());
        model.addAttribute("titulo", "Registrar Articulo");
        model.addAttribute("accion", "/articulos/guardar");

        return "articulos/formulario";
    }

    @PostMapping("/articulos/guardar")
    public String guardarArticulo(@ModelAttribute("articuloDTO") ArticuloDTO articuloDTO) {
        articuloService.guardarArticulo(articuloDTO);
        
        return "redirect:/articulos";
    }
    
    @GetMapping("/articulos/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("articuloDTO", articuloService.obtenerPorId(id));
        model.addAttribute("titulo", "Editar Articulo");
        model.addAttribute("accion", "/articulos/editar/"+id);

        return "articulos/formulario";
    }

    @PostMapping("/articulos/editar/{id}")
    public String editarArticulo(@PathVariable("id") Long id, @ModelAttribute("articuloDTO") ArticuloDTO articuloDTO) {
        articuloService.editarArticulo(id, articuloDTO);
        
        return "redirect:/articulos";
    }
    
    @GetMapping("/articulos/eliminar/{id}")
    public String eliminarArticulo(@PathVariable("id") Long id) {
        articuloService.eliminarArticuloPorId(id);

        return "redirect:/articulos";
    }
    
}
