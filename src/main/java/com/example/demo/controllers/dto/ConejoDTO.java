package com.example.demo.controllers.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConejoDTO {

	private Long id;
	
	private String nombre;
	private String sexo;
	private Double peso;
	private boolean activo;
	private String nota;

	private String nombreImagen;
	private LocalDate fechaNacimiento;
	private Integer totalNacimientos;
	private Integer totalGazapos;

	private MultipartFile imagen;
	private RazaDTO raza;
}
