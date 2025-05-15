package com.example.demo.controllers;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.services.IConejoService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class ConejoController {

	@Autowired
	private IConejoService conejoService;

	// °°°°°°°°°°°°°°°°°°°°°°°°°° LISTAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/conejos")
	public String obtenerConejos(
		Model model,
		@RequestParam(required = false) String nombre,
		@RequestParam(required = false) String sexo) {

		List<ConejoDTO> listaConejos = conejoService.buscarPorFiltros(nombre, sexo);

		model.addAttribute("listaConejos", listaConejos);
		model.addAttribute("nombre", nombre);
		model.addAttribute("sexo", sexo);

		return "conejos/lista"; // Retornar al archivo html
	}

	// °°°°°°°°°°°°°°°°°°°°°°°°°° GUARDAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/conejos/crear")
	public String mostrarFormularioCrear(Model model) {
		ConejoDTO conejoDTO = new ConejoDTO();
		model.addAttribute("conejoDTO", conejoDTO); // Enviar ConejoDTO vacio
		model.addAttribute("titulo", "Registrar Conejo"); // Titulo del formulario
		model.addAttribute("accion", "/conejos/guardar"); // POST. Enviar endpoint para guardar

		return "conejos/formulario"; // Retornar al archivo html
	}

	@PostMapping("/conejos/guardar")
	public String guardarConejo(
			Model model,
			@RequestParam("nombre") String nombre,
			@RequestParam("imagen") MultipartFile imagen,
			@RequestParam("raza") String raza,
			@RequestParam("peso") double peso,
			@RequestParam("sexo") String sexo,
			@RequestParam("salud") boolean salud) {

		if (conejoService.existsByNombre(nombre)) {
			ConejoDTO conejoDTO = new ConejoDTO();
			conejoDTO.setNombre(nombre);
			conejoDTO.setRaza(raza);
			conejoDTO.setPeso(peso);
			conejoDTO.setSexo(sexo);
			conejoDTO.setSalud(salud);
		
			model.addAttribute("mensaje", "El conejo con el nombre: " + nombre + " ya se encuentra registrado.");
			model.addAttribute("conejoDTO", conejoDTO); // ¡Agrega el objeto aquí!
			model.addAttribute("titulo", "Registrar Conejo");
			model.addAttribute("accion", "/conejos/guardar");
		
			return "conejos/formulario";
		}

		String extension = null;
		String nombreImagen = null;

		// Obtenemos la extension de la imagen
		extension = FilenameUtils.getExtension(imagen.getOriginalFilename());
		// Cambiar nombre de la imagen a nombre del conejo + extension
		nombreImagen = nombre + "." + extension;

		try {
			// Definir la ruta donde se guardará la imagen
			Path ruta = Paths.get("src/main/resources/static/img/conejos", nombreImagen);
			// Guardar el archivo en la ruta definida
			Files.write(ruta, imagen.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
			return "Error al guardar la imagen del conejo";
		}

		// Asignar valores al ConejoDTO
		ConejoDTO conejoDTO = new ConejoDTO();
		conejoDTO.setNombre(nombre);
		conejoDTO.setImagen(nombreImagen); // Guardamos solo el nombre
		conejoDTO.setRaza(raza);
		conejoDTO.setPeso(peso);
		conejoDTO.setSexo(sexo);
		conejoDTO.setSalud(salud);

		// Guardar conejo en la BD
		conejoService.guardarConejo(conejoDTO);

		return "redirect:/conejos"; // Redireccionar al endpoint: "/lista"
	}

	// °°°°°°°°°°°°°°°°°°°°°°°°°° EDITAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/conejos/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
		ConejoDTO conejoDTO = conejoService.obtenerConejoById(id);
		model.addAttribute("conejoDTO", conejoDTO); // Enviar ConejoDTO con la informacion
		model.addAttribute("titulo", "Editar Conejo"); // Enviar titulo del formulario
		model.addAttribute("accion", "/conejos/editar/" + id); // POST. Enviar endpoint para editar

		return "conejos/formulario"; // Retornar al archivo html
	}

	@PostMapping("/conejos/editar/{id}")
	public String editarConejo(
			@PathVariable("id") Long id,
			@RequestParam("nombre") String nombre,
			@RequestParam("imagen") MultipartFile imagen,
			@RequestParam("raza") String raza,
			@RequestParam("peso") double peso,
			@RequestParam("sexo") String sexo,
			@RequestParam("salud") boolean salud) {

		ConejoDTO conejoDTO = conejoService.obtenerConejoById(id);

		String extension = null;
		String nombreImagen = null;
		Path rutaNueva = null;
		Path rutaAntigua = null;

		if (!nombre.equals(conejoDTO.getNombre())) {	//Se cambio nombre
			if (conejoService.existsByNombre(nombre)) {
				return "El conejo con el nombre: " + nombre + ", ya se encuentra registrado.";
			}

			// nombre actual de la imagen
			rutaAntigua = Paths.get("src/main/resources/static/img/conejos", conejoDTO.getImagen());

			if(imagen != null && !imagen.isEmpty()){	//Se cambio imagen
				extension = FilenameUtils.getExtension(imagen.getOriginalFilename());	// nombre nuevo de la imagen
				System.out.println("------------------"+extension+"------------------");

			}else{										// No se cambio imagen
				extension = FilenameUtils.getExtension(conejoDTO.getImagen());	// nombre nuevo de la imagen
				System.out.println("------------------"+conejoDTO.getImagen()+"------------------");
			}

			nombreImagen = nombre + "." + extension;
			rutaNueva = Paths.get("src/main/resources/static/img/conejos", nombreImagen);

			try {
				if(imagen != null && !imagen.isEmpty()){
					Files.deleteIfExists(rutaAntigua);	// Eliminar imagen actual
					Files.write(rutaNueva, imagen.getBytes());	// Crear nueva imagen

				}else{
					Files.move(rutaAntigua, rutaNueva, StandardCopyOption.REPLACE_EXISTING);	//Modificar el nombre de la imagen
				}

			} catch (Exception e) {
				e.printStackTrace();
				return "Error al modificar la imagen del conejo, cambiando el nombre";
			}

			// Mapear nuevo nombre y nombreImagen al ConejoDTO
			conejoDTO.setNombre(nombre);
			conejoDTO.setImagen(nombreImagen);
		}

		if (nombre.equals(conejoDTO.getNombre())) {	//No se cambio nombre
			if(imagen != null && !imagen.isEmpty()){	//Se cambio imagen

				// nombre actual de la imagen
				rutaAntigua = Paths.get("src/main/resources/static/img/conejos", conejoDTO.getImagen());

				// nombre nuevo de la imagen
				extension = FilenameUtils.getExtension(imagen.getOriginalFilename());
				nombreImagen = conejoDTO.getNombre() +"."+extension;
				rutaNueva = Paths.get("src/main/resources/static/img/conejos", nombreImagen);

				try {
					Files.deleteIfExists(rutaAntigua);	//Eliminar imagen actual
					Files.write(rutaNueva, imagen.getBytes());	//Crear nueva imagen
				} catch (Exception e) {
					return ""+rutaNueva;
					//return "Error al modificar la imagen del conejo, sin cambiar el nombre";
				}

			//Mapear nombreImagen al ConejoDTO
			conejoDTO.setImagen(nombreImagen);
			}
		}

		// Mapear nuevos valores al conejoDTO
		conejoDTO.setRaza(raza);
		conejoDTO.setPeso(peso);
		conejoDTO.setSexo(sexo);
		conejoDTO.setSalud(salud);

		// Editar conejo en la BD
		conejoService.editarConejo(id, conejoDTO);

		return "redirect:/conejos"; // Redireccionar al endpoint: "conejos/lista"
	}

	// °°°°°°°°°°°°°°°°°°°°°°°°°° ELIMINAR °°°°°°°°°°°°°°°°°°°°°°°°°°//

	@GetMapping("/conejos/eliminar/{id}")
	public String eliminarConejo(@PathVariable("id") Long id) {

		// Obtener ConejoDTO por id
		ConejoDTO conejoDTO = conejoService.obtenerConejoById(id);

		// Obtener nombre de la imagen del ConejoDTO
		Path ruta = Paths.get("src/main/resources/static/img/conejos", conejoDTO.getImagen());

		// Eliminar imagen si existe
		try {
			Files.deleteIfExists(ruta);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error al eliminar la imagen del conejo";
		}

		// Eliminar conejo en la BD
		conejoService.eliminarConejoById(id);

		return "redirect:/conejos"; // Redireccionar al endpoint: "conejos/lista"
	}

	/*
	 * //----- APIREST -----//
	 * 
	 * // obtener conejos
	 * 
	 * @GetMapping("/conejos")
	 * public ResponseEntity<List<ConejoModel>> obtenerConejos() {
	 * List<ConejoModel> lista = conejoService.obtenerConejos();
	 * 
	 * if (lista.isEmpty()) {
	 * return ResponseEntity.noContent().build(); // 204 no content
	 * }
	 * return ResponseEntity.ok(lista); // 200 ok
	 * }
	 * 
	 * // buscar por id
	 * 
	 * @GetMapping("/conejos/{id}")
	 * public ResponseEntity<ConejoModel> buscarPorId(@PathVariable("id") Long id) {
	 * ConejoModel conejoBuscado = conejoService.buscarPorId(id);
	 * 
	 * if (conejoBuscado == null) {
	 * return ResponseEntity.notFound().build(); // 404 not found
	 * }
	 * return ResponseEntity.ok(conejoBuscado); // 200 ok
	 * }
	 * 
	 * 
	 * // guardar conejo
	 * 
	 * @PostMapping("/conejos")
	 * public ResponseEntity<ConejoDTO> guardarConejo(@RequestBody @Valid ConejoDTO
	 * conejoDTO){
	 * return new ResponseEntity<>(conejoService.guardarConejo(conejoDTO),
	 * HttpStatus.CREATED);
	 * }
	 * 
	 * // guardar o modificar conejo
	 * 
	 * @PostMapping("/conejos")
	 * public ResponseEntity<?> guardarConejo(@RequestBody ConejoModel conejoModel)
	 * {
	 * String nombre = conejoModel.getNombre();
	 * if (conejoService.existeConejoPorNombre(nombre)) {
	 * return
	 * ResponseEntity.status(HttpStatus.CONFLICT).body("El conejo con nombre: "
	 * +nombre+", ya existe en la BD.");
	 * }
	 * return ResponseEntity.ok(conejoService.guardarConejo(conejoModel));
	 * }
	 * 
	 * // eliminar conejo
	 * 
	 * @DeleteMapping("/conejos/{id}")
	 * public boolean eliminarConejo(@PathVariable("id") Long id) {
	 * ConejoModel conejoBuscado = conejoService.buscarPorId(id);
	 * 
	 * if (conejoBuscado != null) {
	 * conejoService.eliminarConejo(id);
	 * return true;
	 * }
	 * return false;
	 * }
	 */
}
