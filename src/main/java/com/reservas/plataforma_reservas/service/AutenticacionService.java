package com.reservas.plataformareservas.service;

import com.reservas.plataformareservas.dto.JwtResponseDto;
import com.reservas.plataformareservas.dto.LoginDto;
import com.reservas.plataformareservas.dto.RegisterDto;
import com.reservas.plataformareservas.entity.Rol;
import com.reservas.plataformareservas.entity.RolEnum;
import com.reservas.plataformareservas.entity.Usuario;
import com.reservas.plataformareservas.repository.RolRepository;
import com.reservas.plataformareservas.repository.UsuarioRepository;
import com.reservas.plataformareservas.security.JwtService;
import com.reservas.plataformareservas.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AutenticacionService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public void registrar(RegisterDto registerDto) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        if (usuarioRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registerDto.getNombre());
        usuario.setEmail(registerDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        usuario.setActivo(true);

        Rol rolCliente = rolRepository.findByNombre(RolEnum.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Rol cliente no encontrado"));
        
        usuario.getRoles().add(rolCliente);

        usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente: {}", registerDto.getEmail());
    }

    public JwtResponseDto login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            String token = jwtService.generarToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList();

            log.info("Login exitoso para usuario: {}", loginDto.getEmail());
            
            return new JwtResponseDto(
                    token,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getNombre(),
                    roles
            );
        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            throw new RuntimeException("Email o contraseña incorrectos");
        }
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}