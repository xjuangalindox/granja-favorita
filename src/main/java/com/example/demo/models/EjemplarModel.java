package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ejemplares")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EjemplarModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_imagen")
    private String nombreImagen;
    private String sexo;
    private boolean vendido;

    // RELATIONS
    @ManyToOne
    @JoinColumn(name = "nacimiento_id", nullable = false)
    private NacimientoModel nacimiento;

    @OneToOne(mappedBy = "ejemplar") // Agregado
    private EjemplarVentaModel ejemplarVenta; // Agregado
}
