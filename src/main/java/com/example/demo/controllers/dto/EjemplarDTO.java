package com.example.demo.controllers.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EjemplarDTO {
    private Long id;

    @JsonIgnore
    private MultipartFile imagen;
    
    private String nombreImagen;
    private String sexo;
    private boolean disponible;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NacimientoDTO nacimiento;
}
