package com.example.demo.controllers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConejoDTO {

	private Long id;

	@NotBlank
	@Size(min = 1, max = 50)
	private String nombre;

	private String imagen;

	@NotBlank
	@Size(min = 1, max = 50)
	private String raza;

	@PositiveOrZero
	@Max(value = 10)
	private double peso;

	@NotBlank
	@Pattern(regexp = "^(?i)(Macho|Hembra)$")
	private String sexo;

	private boolean salud = true;

}
