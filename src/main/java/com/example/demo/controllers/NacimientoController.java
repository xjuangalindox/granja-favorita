package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
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

	@GetMapping("/nacimientos/por-monta/{idMonta}")
	public String obtenerNacimientoPorMonta(Model model, @PathVariable("idMonta") Long idMonta) {
		NacimientoDTO nacimientoDTO = nacimientoService.obtenerNacimientoPorIdMonta(idMonta);
		List<NacimientoDTO> listaNacimientos = new ArrayList<>();
		
		if(nacimientoDTO != null){
			listaNacimientos.add(nacimientoDTO);
		}
		model.addAttribute("listaNacimientos", listaNacimientos);

		return "nacimientos/lista";	// Retornar al html
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/nacimientos/crear")
	public String formularioCrear(Model model) {
		model.addAttribute("nacimientoDTO", new NacimientoDTO());	// Enviar nacimientoDTO
		model.addAttribute("titulo", "Registrar Nacimiento");	// Enviar titulo del formulario
		model.addAttribute("accion", "/nacimientos/guardar");	// Enviar endpoint para guardar el nacimiento
		model.addAttribute("listaMontas", montaService.obtenerMontas()); // Enviar lista de montas

		return "nacimientos/formulario";	//	Retornar al formulario html
	}

	@PostMapping("/nacimientos/guardar")
	public String postMethodName(@ModelAttribute("nacimientoDTO") NacimientoDTO nacimientoDTO) {
		nacimientoService.guardarNacimiento(nacimientoDTO);
		
		return "redirect:/nacimientos";	// Redireccionar al endpoint: /nacimientos
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/nacimientos/editar/{id}")
	public String formularioEditar(@PathVariable("id") Long id, Model model) {
		NacimientoDTO nacimientoDTO = nacimientoService.obtenerNacimientoById(id);	// Obtener plantilla con informacion de la BD
		model.addAttribute("nacimientoDTO", nacimientoDTO);	// Enviar plantilla con informacion
		model.addAttribute("titulo", "Editar Nacimiento");	// Enviar titulo del formulario
		model.addAttribute("accion", "/nacimientos/editar/"+id);	// Enviar enpoint para editar el nacimiento

		return "nacimientos/formulario";	// Retornar al formulatio html
	}
	
	@PostMapping("/nacimientos/editar/{id}")
	public String postMethodName(@PathVariable("id") Long id, @ModelAttribute("nacimientoDTO") NacimientoDTO nacimientoDTO) {
		nacimientoService.editarNacimiento(id, nacimientoDTO);	// Enviar informacion al servicio para editar		

		return "redirect:/nacimientos";	// Redireccionar al endpoint: /nacimientos
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/nacimientos/eliminar/{id}")
	public String eliminarNacimiento(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		if(nacimientoService.existsById(id)){

			try {
				nacimientoService.eliminarNacimientoById(id);
				// Eliminar cada img de cada ejemplar del nacimiento
				redirectAttributes.addFlashAttribute("ok", "Nacimiento eliminado correctamente.");

			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("error", "Ocurrió un error al eliminar el nacimiento.");
			}

		}else{
			redirectAttributes.addFlashAttribute("error", "El nacimiento con ID "+id+" no fue encontrado.");
		}

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
