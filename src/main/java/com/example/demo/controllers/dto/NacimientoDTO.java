package com.example.demo.controllers.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
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

	@NotNull
	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(nullable = false)
	private LocalDate fecha_nacimiento;

	@NotNull
	@PositiveOrZero
	@Max(value = 15)
	@Column(nullable = false)
	private int gazapos_vivos;

	@NotNull
	@PositiveOrZero
	@Max(value = 15)
	@Column(nullable = false)
	private int gazapos_muertos;

	@Size(min = 0, max = 50)
	private String nota;
	
	// Relacion con MontaDTO
	
	@NotNull
	@Valid
	private MontaDTO monta;
}
