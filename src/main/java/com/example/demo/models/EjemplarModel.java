package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    private String sexo;    //"Macho" o "Hembra"
    private String nombreImagen;
    private double precio;

    @ManyToOne
    private NacimientoModel nacimiento;

    // No almacena ningun valor, obligatorio para la relacion
    @ManyToOne
    private VentaModel venta;
}
