package com.example.demo.controllers.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NacimientoDTO {

	private Long id;

	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaNacimiento;

	@PositiveOrZero
	@Max(value = 15)
	private Integer gazaposVivos;

	@PositiveOrZero
	@Max(value = 15)
	private Integer gazaposMuertos;

	@Size(min = 0, max = 50)
	private String nota;
	
	// Relacion con MontaDTO
	@Valid
	private MontaDTO monta;
}
