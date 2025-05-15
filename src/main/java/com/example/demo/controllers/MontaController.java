package com.example.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.models.MontaModel;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.repositories.NacimientoRepository;
import com.example.demo.services.IConejoService;
import com.example.demo.services.IMontaService;
import com.example.demo.services.INacimientoService;

import jakarta.validation.Valid;

@Controller
public class MontaController {

	@Autowired
	private INacimientoService nacimientoService;

	@Autowired
	private NacimientoRepository nacimientoRepository;

	@Autowired
	private IMontaService montaService;

	@Autowired
	private IConejoService conejoService;

	//°°°°°°°°°°°°°°°°°°°°°°°°°° LISTAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/montas")
	public String obtenerMontas(Model model){
		List<MontaDTO> listaMontas = montaService.obtenerMontas();
		model.addAttribute("listaMontas", listaMontas);

		for (MontaDTO montaDTO : listaMontas) {
			System.out.println(montaDTO.toString());
		}

		return "montas/lista";	//Retornar al html
	}

	//°°°°°°°°°°°°°°°°°°°°°°°°°° GUARDAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/montas/crear")
	public String formularioCrear(Model model){
		MontaDTO montaDTO = new MontaDTO();	//Creacion de plantilla
		model.addAttribute("montaDTO", montaDTO);	// Enviar plantilla
		model.addAttribute("titulo", "Registrar Monta");	// Enviar titulo del formulario
		model.addAttribute("accion", "/montas/guardar");	// Enviar endpoint para guardar
		model.addAttribute("listaEstatus", EstatusMonta.values());	// Enviar valores del enum

		// Obtener lista de conejos machos y hembras
		List<ConejoDTO> machos = conejoService.obtenerConejosPorSexo("Macho");
		List<ConejoDTO> hembras = conejoService.obtenerConejosPorSexo("Hembra");

		// Enviar listas al formulario
		model.addAttribute("listaMachos", machos);
		model.addAttribute("listaHembras", hembras);

		return "montas/formulario";	// Retornar al html
	}

	@PostMapping("/montas/guardar")
	public String guardarMonta(@ModelAttribute("conejoDTO") MontaDTO montaDTO) {
		montaService.guardarMonta(montaDTO);
		
		return "redirect:/montas";	// Redireccionar al endpoint: "/"
	}

	//°°°°°°°°°°°°°°°°°°°°°°°°°° EDITAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/montas/editar/{id}")
	public String formularioEditar(Model model, @PathVariable("id") Long id){
		MontaDTO montaDTO = montaService.obtenerMontaById(id);	// Obtener informacion de la BD
		model.addAttribute("montaDTO", montaDTO);	// Enviar plantilla con la informacion
		model.addAttribute("titulo", "Editar Monta");	// Enviar titulo del formulario
		model.addAttribute("accion", "/montas/editar/"+id);	// Enviar endpoint para editar

		// Obtener lista de conejos machos y hembras
		List<ConejoDTO> machos = conejoService.obtenerConejosPorSexo("Macho");
		List<ConejoDTO> hembras = conejoService.obtenerConejosPorSexo("Hembra");

		// Enviar listas al formulario
		model.addAttribute("listaMachos", machos);
		model.addAttribute("listaHembras", hembras);

		return "montas/formulario";	// Retornar al html
	}

	@PostMapping("/montas/editar/{id}")
	public String editarMonta(@ModelAttribute("montaDTO") MontaDTO montaDTO, @PathVariable("id") Long id){
		montaService.editarMonta(id, montaDTO);	// Enviar id e informacion al servicio

		return "redirect:/montas";	// Redireccionar el endpoint: "/"
	}


	//°°°°°°°°°°°°°°°°°°°°°°°°°° ELIMINAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("montas/eliminar/{id}")
	public String eliminarMonta(@PathVariable("id") Long id){
		montaService.eliminarMontaById(id);

		return "redirect:/montas";	//Redireccionar al endpoint: "/"
	}






	/*
	// Obtener montas
	@GetMapping("/montas")
	public ResponseEntity<List<MontaModel>> obtenerMontas() {
		List<MontaModel> lista = montaService.obtenerMontas();

		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(lista);
	}

	// Guardar o modificar monta
	@PostMapping("/montas")
	public ResponseEntity<?> guardarMonta(@RequestBody MontaModel montaModel) {
		return ResponseEntity.ok(montaService.guardarMonta(montaModel));
	}

	// Eliminar monta por id
	@DeleteMapping("/montas/{id}")
	public boolean eliminarMonta(@PathVariable("id") Long id) {
		MontaModel montaBuscada = montaService.buscarPorId(id);

		if (montaBuscada != null) {
			montaService.eliminarMontaPorId(id);
			return true;
		}
		return false;
	}

	// Buscar monta por id
	@GetMapping("/montas/{id}")
	public ResponseEntity<MontaModel> buscarPorId(@PathVariable("id") Long id) {
		MontaModel montaBuscada = montaService.buscarPorId(id);

		if (montaBuscada == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(montaBuscada);
	}*/

}
