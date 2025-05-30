package com.example.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.services.IConejoService;
import com.example.demo.services.IRazaService;
import com.example.demo.util.ArchivoUtil;

@Controller
public class ConejoController {

	@Autowired
	private IConejoService conejoService;

	@Autowired
	private IRazaService razaService;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static final String RUTA_CONEJOS = "src/main/resources/static/img/conejos";

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/conejos")
	public String obtenerConejos(Model model) {
		List<ConejoDTO> listaConejos = conejoService.obtenerConejos();

		model.addAttribute("listaConejos", listaConejos);
		return "conejos/lista"; // Retornar al archivo html
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/conejos/crear")
	public String mostrarFormularioCrear(Model model) {
		ConejoDTO conejoDTO = new ConejoDTO();
		conejoDTO.setActivo(true);

		model.addAttribute("conejoDTO", conejoDTO); // Enviar ConejoDTO vacio
		model.addAttribute("listaRazas", razaService.obtenerRazas()); // Enviar lista de razas
		model.addAttribute("titulo", "Registrar Conejo"); // Titulo del formulario
		model.addAttribute("accion", "/conejos/guardar"); // POST. Enviar endpoint para guardar

		return "conejos/formulario"; // Retornar al archivo html
	}

	@PostMapping("/conejos/guardar")
	public String guardarConejo(@ModelAttribute("conejoDTO") ConejoDTO conejoDTO, Model model, RedirectAttributes redirectAttributes) {

		if (conejoService.existsByNombre(conejoDTO.getNombre())) {
			model.addAttribute("conejoDTO", conejoDTO); // Reenviar ConejoDTO con informacion
			model.addAttribute("listaRazas", razaService.obtenerRazas()); // Reenviar lista de razas
			model.addAttribute("titulo", "Registrar Conejo"); // Reenviar el titulo
			model.addAttribute("accion", "/conejos/guardar"); // Reenviar EndPoint
			
			model.addAttribute("mensaje", "El conejo con el nombre: "+conejoDTO.getNombre()+", ya se encuentra registrado.");
			return "conejos/formulario";
		}

		String extension = ArchivoUtil.obtenerExtensionImagen(conejoDTO.getImagen());
		String nombreImagen = ArchivoUtil.crearNombreImagen(conejoDTO.getNombre(), extension);
		Path ruta = ArchivoUtil.crearRuta(RUTA_CONEJOS, nombreImagen);

		try {
			Files.write(ruta, conejoDTO.getImagen().getBytes());
			conejoDTO.setNombreImagen(nombreImagen);

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("mensaje", "Error al guardar la imagen en el proyecto.");
			return "conejos/formulario";
		}

		conejoService.guardarConejo(conejoDTO);
		redirectAttributes.addFlashAttribute("ok", "Conejo registrado correctamente.");
		return "redirect:/conejos";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/conejos/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
		ConejoDTO conejoDTO = conejoService.obtenerConejoById(id);

		model.addAttribute("conejoDTO", conejoDTO); // Enviar ConejoDTO con la informacion
		model.addAttribute("listaRazas", razaService.obtenerRazas()); // Enviar lista de razas
		model.addAttribute("titulo", "Editar Conejo"); // Enviar titulo del formulario
		model.addAttribute("accion", "/conejos/editar/" + id); // POST. Enviar endpoint para editar

		return "conejos/formulario"; // Retornar al archivo html
	}

	@PostMapping("/conejos/editar/{id}")
	public String editarConejo(@PathVariable("id") Long id, @ModelAttribute("conejoDTO") ConejoDTO conejoDTO, Model model, RedirectAttributes redirectAttributes){
		ConejoDTO conejo = conejoService.obtenerConejoById(id);

		Path rutaAntigua = ArchivoUtil.crearRuta(RUTA_CONEJOS, conejoDTO.getNombreImagen());
		Path ruta = null;
		String extension = "";
		String nombreImagen = "";

		// SE CAMBIO NOMBRE
		if(!conejoDTO.getNombre().equals(conejo.getNombre())){
			// Existe existente
			if(conejoService.existsByNombre(conejoDTO.getNombre())){
				model.addAttribute("conejoDTO", conejoDTO);
				model.addAttribute("listaRazas", razaService.obtenerRazas());
				model.addAttribute("titulo", "Editar Conejo");
				model.addAttribute("accion", "/conejos/editar/"+id);

				model.addAttribute("mensaje", "El conejo con el nombre: "+conejoDTO.getNombre()+", ya se encuentra registrado.");
				return "conejos/formulario";
			}

			// Se cambio imagen
			if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
				extension = ArchivoUtil.obtenerExtensionImagen(conejoDTO.getImagen());
				nombreImagen = ArchivoUtil.crearNombreImagen(conejoDTO.getNombre(), extension);
				ruta = ArchivoUtil.crearRuta(RUTA_CONEJOS, nombreImagen);

				try {
					Files.deleteIfExists(rutaAntigua);
					Files.write(ruta, conejoDTO.getImagen().getBytes());
					conejoDTO.setNombreImagen(nombreImagen);

				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("mensaje", "Error al reemplazar la imagen en el proyecto.");
					return "conejos/formulario";
				}

			// No se cambio imagen
			}else{
				extension = ArchivoUtil.obtenerExtensionTexto(conejoDTO.getNombreImagen());
				nombreImagen = ArchivoUtil.crearNombreImagen(conejoDTO.getNombre(), extension);
				ruta = ArchivoUtil.crearRuta(RUTA_CONEJOS, nombreImagen);

				try {
					Files.move(rutaAntigua, ruta, StandardCopyOption.REPLACE_EXISTING);
					conejoDTO.setNombreImagen(nombreImagen);
				
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("mensaje", "Error el reemplazar el nombre de la imagen en el proyecto.");
					return "conejos/formulario";
				}
			}
		}

		// NO SE CAMBIO NOMBRE
		if(conejoDTO.getNombre().equals(conejo.getNombre())){
			// Se cambio imagen
			if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
				extension = ArchivoUtil.obtenerExtensionImagen(conejoDTO.getImagen());
				nombreImagen = ArchivoUtil.crearNombreImagen(conejoDTO.getNombre(), extension);
				ruta = ArchivoUtil.crearRuta(RUTA_CONEJOS, nombreImagen);

				try {
					Files.deleteIfExists(rutaAntigua);
					Files.write(ruta, conejoDTO.getImagen().getBytes());
					conejoDTO.setNombreImagen(nombreImagen);

				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("mensaje", "Error al reemplazar la imagen en el proyecto.");
					return "conejos/formulario";
				}
			}
		}

		conejoService.editarConejo(id, conejoDTO);
		redirectAttributes.addFlashAttribute("ok", "Conejo modificado correctamente.");
		return "redirect:/conejos";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/conejos/eliminar/{id}")
	public String eliminarConejo(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		if(conejoService.existsById(id)){
			ConejoDTO conejoDTO = conejoService.obtenerConejoById(id);
			Path ruta = ArchivoUtil.crearRuta(RUTA_CONEJOS, conejoDTO.getNombreImagen());

			try {
				conejoService.eliminarConejoById(id);
				Files.deleteIfExists(ruta);
				redirectAttributes.addFlashAttribute("ok", "Conejo eliminado correctamente.");

			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("error", "Ocurrió un error al eliminar el conejo.");
			}

		}else{
			redirectAttributes.addFlashAttribute("error", "El conejo con ID "+id+" no fue encontrado.");
		}

		return "redirect:/conejos";
	}
}
