package com.example.demo.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticuloDTO {
    private Long id;

    private String nombre;
    private String presentacion;    //1 kg, Bolsa (200 gramos)
    private double precio;
}
