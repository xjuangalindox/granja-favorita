package com.example.demo.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nacimientos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NacimientoModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(name = "gazapos_vivos")
	private Integer gazaposVivos;

	@Column(name = "gazapos_muertos")
	private Integer gazaposMuertos;

	private String nota;

	// Relacion con MontaModel
	@OneToOne(targetEntity = MontaModel.class)
	@JoinColumn(name = "id_monta")
	private MontaModel monta;
	
}