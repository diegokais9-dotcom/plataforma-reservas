package com.reservas.plataformareservas.config;

import com.reservas.plataformareservas.entity.Rol;
import com.reservas.plataformareservas.entity.RolEnum;
import com.reservas.plataformareservas.entity.Usuario;
import com.reservas.plataformareservas.repository.RolRepository;
import com.reservas.plataformareservas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Rol rolAdmin = rolRepository.findByNombre(RolEnum.ROLE_ADMIN)
                .orElseGet(() -> rolRepository.save(new Rol(RolEnum.ROLE_ADMIN)));
        rolRepository.findByNombre(RolEnum.ROLE_CLIENTE)
                .orElseGet(() -> rolRepository.save(new Rol(RolEnum.ROLE_CLIENTE)));

        if (!usuarioRepository.existsByEmail("admin@hotel.com")) {
            Usuario admin = new Usuario("admin@hotel.com", "Administrador",
                    passwordEncoder.encode("Admin1234"));
            admin.getRoles().add(rolAdmin);
            usuarioRepository.save(admin);
        }
    }
}