package com.example.demo.models;

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
@Table(name = "articulo_venta")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticuloVentaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer cantidad;
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "articulo_id")  // Relación con el artículo
    private ArticuloModel articulo;

    // No almacena ningun valor, obligatorio para la relacion
    @ManyToOne
    @JoinColumn(name = "venta_id")  // Esto crea la relación de clave foránea
    private VentaModel venta;
}
