package com.reservas.plataformareservas.controller;

import com.reservas.plataformareservas.dto.HabitacionDto;
import com.reservas.plataformareservas.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public List<HabitacionDto> listar() {
        return habitacionService.listar();
    }

    @GetMapping("/disponibles")
    public List<HabitacionDto> disponibles(@RequestParam(defaultValue = "1") Integer capacidad) {
        return habitacionService.listarDisponibles(capacidad);
    }

    @GetMapping("/{id}")
    public HabitacionDto obtener(@PathVariable Long id) {
        return habitacionService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<HabitacionDto> crear(@Valid @RequestBody HabitacionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitacionService.crear(dto));
    }

    @PutMapping("/{id}")
    public HabitacionDto actualizar(@PathVariable Long id, @Valid @RequestBody HabitacionDto dto) {
        return habitacionService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        habitacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}