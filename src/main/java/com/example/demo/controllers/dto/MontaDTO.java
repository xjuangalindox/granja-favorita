package com.example.demo.controllers.dto;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.models.enums.EstatusMonta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MontaDTO {

	private Long id;

	@Size(min = 0, max = 50)
	private String nota;

	@PastOrPresent(message = "La fecha no puede ser futura")
	@DateTimeFormat(pattern = "yyyy-MM-dd") // Necesario para manejar un solo formaro en el frontend
	private LocalDate fechaMonta;

	@PositiveOrZero
	@Max(value = 10)
	private Integer cantidadMontas;

	private EstatusMonta estatus;

	// ✅ Campo auxiliar para vista (no se guarda en DB)
	private boolean tieneNacimiento;
	
	// Relacion con ConejoDTO
	
	@Valid
	private ConejoDTO hembra;

	@Valid
	private ConejoDTO macho;
}
