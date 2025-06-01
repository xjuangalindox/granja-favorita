package com.example.demo.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class ArchivoUtil {
    
	// Obtener nombre base de imagen
	public static String obtenerNombreBaseImagen(MultipartFile imagen){
		return FilenameUtils.getBaseName(imagen.getOriginalFilename());
	}

    // Obtener extension de imagen
	public static String obtenerExtensionImagen(MultipartFile imagen){
		return FilenameUtils.getExtension(imagen.getOriginalFilename());
	}

	// Obtener extension de texto
	public static String obtenerExtensionTexto(String texto){
		return FilenameUtils.getExtension(texto);
	}

	// Crear nombre de la imagen
	public static String crearNombreImagen(String nombre, String extension){
		return nombre + "." + extension;
	}

	// Crear ruta con nombreImagen
	public static Path crearRuta(String ruta, String nombreImagen){
		return Paths.get(ruta, nombreImagen);
	}
}
