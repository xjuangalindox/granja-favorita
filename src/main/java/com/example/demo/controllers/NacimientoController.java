package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;
import com.example.demo.services.IMontaService;
import com.example.demo.services.INacimientoService;

@Controller
public class NacimientoController {

	@Autowired
	private INacimientoService nacimientoService;

	@Autowired
	private IMontaService montaService;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/nacimientos")
	public String obtenerNacimientos(Model model) {
		List<NacimientoDTO> listaNacimientos = nacimientoService.obtenerNacimientos();
		model.addAttribute("listaNacimientos", listaNacimientos);

		return "nacimientos/lista"; // Retornar al html
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/nacimientos/editar/por-monta/{idMonta}")
	public String obtenerNacimientoPorMonta(@PathVariable("idMonta") Long idMonta, Model model, RedirectAttributes redirectAttributes) {
		
		Optional<MontaDTO> montaOpt = montaService.obtenerMontaById(idMonta);
		if(montaOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "La monta con ID " + idMonta + " no fue encontrada.");
			return "redirect:/montas";	
		}

		MontaDTO montaDTO = montaOpt.get();
		Optional<NacimientoDTO> nacimientoOpt = nacimientoService.findByMonta(montaDTO);

		if(nacimientoOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "La monta con ID "+ idMonta+" no pertenece a ningún nacimiento.");
			return "redirect:/montas";	
		}

		NacimientoDTO nacimientoDTO = nacimientoOpt.get();

		model.addAttribute("nacimientoDTO", nacimientoDTO);
		model.addAttribute("titulo", "Editar Nacimiento");
		model.addAttribute("accion", "/nacimientos/editar/"+nacimientoDTO.getId());
		model.addAttribute("listaMontas", montaService.obtenerMontas());
		return "nacimientos/formulario";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/nacimientos/crear")
	public String formularioCrear(Model model, RedirectAttributes redirectAttributes,
		@RequestParam(name = "idMonta", required = false) Long idMonta) {

		NacimientoDTO nacimientoDTO = new NacimientoDTO();
		
		if(idMonta != null){
			Optional<MontaDTO> montaOpt = montaService.obtenerMontaById(idMonta);
			if(montaOpt.isEmpty()){
				redirectAttributes.addFlashAttribute("error", "La monta con ID "+idMonta+" no fue encontrada.");
				return "redirect:/montas";
			}
			nacimientoDTO.setMonta(montaOpt.get());
		}

		model.addAttribute("nacimientoDTO", nacimientoDTO);	// Enviar nacimientoDTO
		model.addAttribute("titulo", "Registrar Nacimiento");	// Enviar titulo del formulario
		model.addAttribute("accion", "/nacimientos/guardar");	// Enviar endpoint para guardar el nacimiento
		model.addAttribute("listaMontas", montaService.obtenerMontas()); // Enviar lista de montas

		return "nacimientos/formulario";	//	Retornar al formulario html
	}

	@PostMapping("/nacimientos/guardar")
	public String guardarNacimiento(@ModelAttribute("nacimientoDTO") NacimientoDTO nacimientoDTO, Model model, RedirectAttributes redirectAttributes) {
		try {
			nacimientoService.guardarNacimiento(nacimientoDTO);
			redirectAttributes.addFlashAttribute("ok", "Nacimiento registrado correctamente.");

		} catch (Exception e) {
			model.addAttribute("nacimientoDTO", nacimientoDTO);	// Enviar nacimientoDTO con informacion
			model.addAttribute("titulo", "Registrar Nacimiento");	// Enviar titulo del formulario
			model.addAttribute("accion", "/nacimientos/guardar");	// Enviar endpoint para guardar el nacimiento
			model.addAttribute("listaMontas", montaService.obtenerMontas()); // Enviar lista de montas
			
			model.addAttribute("mensaje", "Ocurrió un error al guardar el nacimiento.");
			return "nacimientos/formulario";
		}

		return "redirect:/nacimientos";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/nacimientos/editar/{id}")
	public String formularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<NacimientoDTO> nacimientoOpt = nacimientoService.obtenerNacimientoById(id);
		
		if(nacimientoOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "El nacimiento con ID " + id + " no fue encontrado.");
			return "redirect:/nacimientos";	
		}

		model.addAttribute("nacimientoDTO", nacimientoOpt.get());
		model.addAttribute("titulo", "Editar Nacimiento");
		model.addAttribute("accion", "/nacimientos/editar/"+id);
		model.addAttribute("listaMontas", montaService.obtenerMontas());
		return "nacimientos/formulario";
	}
	
	@PostMapping("/nacimientos/editar/{id}")
	public String editarNacimiento(@PathVariable("id") Long id, @ModelAttribute("nacimientoDTO") NacimientoDTO nacimientoDTO,
		@RequestParam(name = "ejemplaresEliminados", required = false) List<Long> ejemplaresEliminados,
		Model model, RedirectAttributes redirectAttributes) {
		
		try {
			nacimientoService.editarNacimiento(id, nacimientoDTO, ejemplaresEliminados);
			redirectAttributes.addFlashAttribute("ok", "Nacimiento modificado correctamente.");

		} catch (Exception e) {
			model.addAttribute("nacimientoDTO", nacimientoDTO);
			model.addAttribute("titulo", "Editar Nacimiento");
			model.addAttribute("accion", "/nacimientos/editar/"+id);
			model.addAttribute("listaMontas", montaService.obtenerMontas());

			model.addAttribute("mensaje", "Ocurrio un error al modificar el nacimiento.");
			return "nacimientos/formulario";
		}

		return "redirect:/nacimientos";	// Redireccionar al endpoint: /nacimientos
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/nacimientos/eliminar/{id}")
	public String eliminarNacimiento(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		try {
			nacimientoService.eliminarNacimientoById(id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Ocurrió un error al eliminar el nacimiento.");
		}

		redirectAttributes.addFlashAttribute("ok", "Nacimiento eliminado correctamente.");
		return "redirect:/nacimientos";
	}
	







	/*
	// Obtener nacimientos
	@GetMapping("/nacimientos")
	public ResponseEntity<List<NacimientoModel>> obtenerNacimientos() {
		List<NacimientoModel> lista = nacimientoService.obtenerNacimientos();

		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build(); // no content
		}
		return ResponseEntity.ok(lista); // ok
	}

	// Buscar nacimiento por id
	@GetMapping("/nacimientos/{id}")
	public ResponseEntity<NacimientoModel> buscarNacimientoPorId(@PathVariable("id") Long id) {
		NacimientoModel nacimientoBuscado = nacimientoService.buscarNacimientoPorId(id);

		if (nacimientoBuscado == null) {
			return ResponseEntity.notFound().build(); // not found
		}
		return ResponseEntity.ok(nacimientoBuscado); // ok
	}

	// Guardar o modificar nacimiento
	@PostMapping("/nacimientos")
	public ResponseEntity<?> guardarNacimiento(@RequestBody NacimientoModel nacimientoModel) {
		return ResponseEntity.ok(nacimientoService.guardarNacimiento(nacimientoModel));/*
		NacimientoModel creado = nacimientoService.guardarNacimiento(nacimientoModel);
		
		// Crear la URI del nuevo recurso creado
		URI location = URI.create("/nacimientos/"+creado.getId());
		
		// Retornar la respuesta con 201 Created y la URI del nuevo nacimiento
		return ResponseEntity.created(location).body(creado);
			
	}
	
	// Modifcar nacimiento
	@PutMapping("/nacimientos/{id}")
	public ResponseEntity<?> modificarNacimientoPorId(@PathVariable("id") Long id, @RequestBody NacimientoModel nacimientoModel){
		try {
			NacimientoModel actualizado = nacimientoService.modificarNacimientoPorId(id, nacimientoModel);
			return ResponseEntity.ok(actualizado);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Eliminar nacimiento
	@DeleteMapping("/nacimientos/{id}")
	public ResponseEntity<?> eliminarNacimiento(@PathVariable("id") Long id) {
		boolean eliminado = nacimientoService.eliminarNacimientoPorId(id);
		if (eliminado) {
			return ResponseEntity.noContent().build(); // 204 Eliminacion correctamente
		}
		return ResponseEntity.notFound().build(); // 404 Error el eliminar
	}*/

}
