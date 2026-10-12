package com.reservas.plataformareservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDto {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long habitacionId;
    private String habitacionNumero;
    private String habitacionTipo;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String estado;
    private BigDecimal total;
}