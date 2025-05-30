package com.example.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.services.IConejoService;
import com.example.demo.services.IMontaService;

import jakarta.validation.Valid;

@Controller
public class MontaController {

	@Autowired
	private IMontaService montaService;

	@Autowired
	private IConejoService conejoService;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/montas")
	public String obtenerMontas(Model model){
		List<MontaDTO> listaMontas = montaService.obtenerMontas();
		model.addAttribute("listaMontas", listaMontas);

		return "montas/lista";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	@GetMapping("/montas/crear")
	public String formularioCrear(Model model){
		model.addAttribute("montaDTO", new MontaDTO());	// Enviar DTO
		model.addAttribute("titulo", "Registrar Monta");	// Enviar titulo del formulario
		model.addAttribute("accion", "/montas/guardar");	// Enviar endpoint para guardar
		model.addAttribute("listaEstatus", EstatusMonta.values());	// Enviar valores del enum
		model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho")); // Enviar lista de machos
		model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra")); // Enviar lista de hembras

		return "montas/formulario";	// Retornar al html
	}

	@PostMapping("/montas/guardar")
	public String guardarMonta(@ModelAttribute("montaDTO") @Valid MontaDTO montaDTO, 
		BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		
		if(result.hasErrors()){
			List<String> errores = result.getAllErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toList());
			model.addAttribute("errores", errores);

			// Vuelve a cargar los datos necesarios para el formulario
			model.addAttribute("montaDTO", montaDTO);
			model.addAttribute("titulo", "Registrar Monta");
			model.addAttribute("accion", "/montas/guardar");
			model.addAttribute("listaEstatus", EstatusMonta.values());
			model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho"));
			model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra"));
			return "montas/formulario";
		}

		montaService.guardarMonta(montaDTO);
		redirectAttributes.addFlashAttribute("ok", "Monta registrada correctamente.");
		return "redirect:/montas";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/montas/editar/{id}")
	public String formularioEditar(@PathVariable("id") Long id, Model model){
		MontaDTO montaDTO = montaService.obtenerMontaById(id);	// Obtener informacion de la BD

		model.addAttribute("montaDTO", montaDTO);	// Enviar plantilla con la informacion
		model.addAttribute("titulo", "Editar Monta");	// Enviar titulo del formulario
		model.addAttribute("accion", "/montas/editar/"+id);	// Enviar endpoint para editar
		model.addAttribute("listaEstatus", EstatusMonta.values()); // Enviar lista de EstatusMonta
		model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho")); // Enviar lista de hembras
		model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra")); // Enviar lista de machos

		return "montas/formulario";	// Retornar al html
	}

	@PostMapping("/montas/editar/{id}")
	public String editarMonta(@PathVariable("id") Long id, @ModelAttribute("montaDTO") @Valid MontaDTO montaDTO, 
		BindingResult result, Model model, RedirectAttributes redirectAttributes){

		if(result.hasErrors()){
			List<String> errores = result.getAllErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toList());
			model.addAttribute("errores", errores);

			// Vuelve a cargar los datos necesarios para el formulario
			model.addAttribute("montaDTO", montaDTO);
			model.addAttribute("titulo", "Editar Monta");
			model.addAttribute("accion", "/montas/editar/"+id);
			model.addAttribute("listaEstatus", EstatusMonta.values());
			model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho"));
			model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra"));
			return "montas/formulario";
		}

		montaService.editarMonta(id, montaDTO);	// Enviar id e informacion al servicio
		redirectAttributes.addFlashAttribute("ok", "Monta modificada correctamente.");
		return "redirect:/montas";	// Redireccionar el endpoint: "/"
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("montas/eliminar/{id}")
	public String eliminarMonta(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
		if(montaService.existsById(id)){	
			
			try {
				montaService.eliminarMontaById(id);
				redirectAttributes.addFlashAttribute("ok", "Monta eliminada correctamente.");			
	
			} catch (Exception e) {
				e.printStackTrace();
				redirectAttributes.addFlashAttribute("error", "Ocurió un error al eliminar la monta.");
			}
	
		}else{
			redirectAttributes.addFlashAttribute("error", "La monta con ID "+id+" no fue encontrada.");
		}

		return "redirect:/montas";
	}
}