package com.example.demo.models;

import java.time.LocalDate;

import com.example.demo.models.enums.EstatusMonta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "montas")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MontaModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate fecha_monta;
	private int cantidad_montas;
	private String nota;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstatusMonta estatus;

	// Relacion con ConejoModel (hembra y macho)

	@ManyToOne(targetEntity = ConejoModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_hembra")
	private ConejoModel hembra;

	@ManyToOne(targetEntity = ConejoModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_macho")
	private ConejoModel macho;
	
}
