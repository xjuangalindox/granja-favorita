package com.example.demo.controllers.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EjemplarDTO {
    private Long id;

    private String sexo;    //"Macho" o "Hembra"
    private MultipartFile imagen; //Solo para recibir img del formulario
    private String nombreImagen;
    private double precio;

    private NacimientoDTO nacimiento;

    // ❌ Esto causaba recursividad
    // private VentaDTO venta;
}
