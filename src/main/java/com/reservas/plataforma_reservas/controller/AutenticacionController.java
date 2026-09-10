package com.reservas.plataformareservas.controller;

import com.reservas.plataformareservas.dto.JwtResponseDto;
import com.reservas.plataformareservas.dto.LoginDto;
import com.reservas.plataformareservas.dto.RegisterDto;
import com.reservas.plataformareservas.service.AutenticacionService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AutenticacionController {

    @Autowired
    private AutenticacionService autenticacionService;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterDto registerDto) {
        try {
            autenticacionService.registrar(registerDto);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario registrado exitosamente");
            response.put("email", registerDto.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error al registrar usuario: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            JwtResponseDto response = autenticacionService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al hacer login: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/verificar")
    public ResponseEntity<?> verificar() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Token válido");
        return ResponseEntity.ok(response);
    }
}