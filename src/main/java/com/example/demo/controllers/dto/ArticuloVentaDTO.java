package com.example.demo.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticuloVentaDTO {
    private Long id;
    private Integer cantidad;
    private double subtotal;

    private ArticuloDTO articulo;
}
