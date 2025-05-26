package com.example.demo.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conejos")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ConejoModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;
	private String sexo;
	private Double peso;
	private boolean activo;
	private String nota;

	@Column(name = "nombre_imagen")
	private String nombreImagen;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(name = "total_nacimientos")
	private Integer totalNacimientos;

	@Column(name = "total_gazapos")
	private Integer totalGazapos;

	@ManyToOne
	@JoinColumn(name = "id_raza")
	private RazaModel raza;
}