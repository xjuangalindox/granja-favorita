package com.example.demo.controllers.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.models.enums.EstatusVenta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VentaDTO {
    private Long id;

    private String nombreCliente;
    private String vinculoContacto; //"FACEBOOK", "WHATSAPP", "FACEBOOK Y WHATSAPP"
    private String telefono;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaEntrega;
    private String lugarEntrega;
    private double totalVenta;
    private String nota;
    private EstatusVenta estatus;   //PENDIENTE, APARTADO, ENTREGADO

    private List<ArticuloVentaDTO> articulos = new ArrayList<>();
    private List<EjemplarDTO> ejemplares = new ArrayList<>();
}
