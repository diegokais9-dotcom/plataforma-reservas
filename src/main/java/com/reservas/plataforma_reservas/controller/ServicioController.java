package com.reservas.plataformareservas.controller;

import com.reservas.plataformareservas.dto.ServicioDto;
import com.reservas.plataformareservas.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public List<ServicioDto> listar() {
        return servicioService.listar();
    }

    @GetMapping("/buscar")
    public List<ServicioDto> buscar(@RequestParam String nombre) {
        return servicioService.buscarPorNombre(nombre);
    }

    @GetMapping("/{id}")
    public ServicioDto obtener(@PathVariable Long id) {
        return servicioService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<ServicioDto> crear(@Valid @RequestBody ServicioDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crear(dto));
    }

    @PutMapping("/{id}")
    public ServicioDto actualizar(@PathVariable Long id, @Valid @RequestBody ServicioDto dto) {
        return servicioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}