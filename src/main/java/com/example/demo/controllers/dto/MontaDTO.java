package com.example.demo.controllers.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.models.enums.EstatusMonta;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MontaDTO {

	private Long id;

	@NotNull
	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(nullable = false)
	private LocalDate fecha_monta;

	@NotNull
	@PositiveOrZero
	@Max(value = 10)
	@Column(nullable = false)
	private int cantidad_montas;

	@Size(min = 0, max = 50)
	private String nota;

	@NotNull
	private EstatusMonta estatus;

	// ✅ Campo auxiliar para vista (no se guarda en DB)
	private boolean tieneNacimiento;
	
	// Relacion con ConejoDTO
	
	@NotNull
	@Valid
	private ConejoDTO hembra;

	@NotNull
	@Valid
	private ConejoDTO macho;
}
