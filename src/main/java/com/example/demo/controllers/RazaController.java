package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.services.IRazaService;

@Controller
public class RazaController {
    
    @Autowired
    private IRazaService razaService;

    @GetMapping("/razas")
    public String obtenerRazas(Model model){
        List<RazaDTO> listaRazas = razaService.obtenerRazas();
        model.addAttribute("listaRazas", listaRazas);

        return "razas/lista";
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/razas/crear")
    public String formularioCrear(Model model){
        model.addAttribute("razaDTO", new RazaDTO());
        model.addAttribute("titulo", "Registrar Raza");
        model.addAttribute("accion", "/razas/guardar");

        return "razas/formulario";
    }

    @PostMapping("/razas/guardar")
    public String guardarRaza(@ModelAttribute("razaDTO") RazaDTO razaDTO, RedirectAttributes redirectAttributes){
        razaService.guardarRaza(razaDTO);
        
        redirectAttributes.addFlashAttribute("mensaje", "Raza registrada correctamente");
        return "redirect:/razas";
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/razas/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id, Model model){
        RazaDTO razaDTO = razaService.obtenerRazaPorId(id);

        model.addAttribute("razaDTO", razaDTO);
        model.addAttribute("titulo", "Editar Raza");
        model.addAttribute("accion", "/razas/editar/"+id);

        return "razas/formulario";
    }

    @PostMapping("/razas/editar/{id}")
    public String editarRaza(@PathVariable("id") Long id, @ModelAttribute("razaDTO") RazaDTO razaDTO, RedirectAttributes redirectAttributes){
        razaService.editarRaza(id, razaDTO);

        redirectAttributes.addFlashAttribute("mensaje", "Raza modificada correctamente");
        return "redirect:/razas";
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/razas/eliminar/{id}")
    public String eliminarRaza(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        if(razaService.eliminarRazaPorId(id)){
            redirectAttributes.addFlashAttribute("mensaje", "Raza eliminada correctamente");
        }else{
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la raza");
        }

        return "redirect:/razas";
    }

}
