package com.reservas.plataformareservas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionDto {

    private Long id;

    @NotBlank(message = "El número de habitación es obligatorio")
    @Size(max = 10, message = "El número no puede pasar de 10 caracteres")
    private String numero;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "SIMPLE|DOBLE|SUITE", message = "El tipo debe ser SIMPLE, DOBLE o SUITE")
    private String tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1")
    private Integer capacidad;

    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioNoche;

    @Pattern(regexp = "DISPONIBLE|OCUPADA|MANTENIMIENTO", message = "El estado debe ser DISPONIBLE, OCUPADA o MANTENIMIENTO")
    private String estado;
}