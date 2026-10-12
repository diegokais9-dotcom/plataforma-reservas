package com.reservas.plataformareservas.controller;

import com.reservas.plataformareservas.dto.ReservaDto;
import com.reservas.plataformareservas.dto.ReservaRequestDto;
import com.reservas.plataformareservas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaDto> crear(@Valid @RequestBody ReservaRequestDto dto,
                                            Authentication authentication) {
        ReservaDto creada = reservaService.crear(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/mis-reservas")
    public List<ReservaDto> misReservas(Authentication authentication) {
        return reservaService.misReservas(authentication.getName());
    }

    @GetMapping
    public List<ReservaDto> listarTodas() {
        return reservaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ReservaDto obtener(@PathVariable Long id, Authentication authentication) {
        return reservaService.obtener(id, authentication.getName());
    }

    @PutMapping("/{id}/cancelar")
    public ReservaDto cancelar(@PathVariable Long id, Authentication authentication) {
        return reservaService.cancelar(id, authentication.getName());
    }
}