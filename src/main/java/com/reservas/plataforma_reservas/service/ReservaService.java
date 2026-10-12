package com.reservas.plataformareservas.service;

import com.reservas.plataformareservas.dto.ReservaDto;
import com.reservas.plataformareservas.dto.ReservaRequestDto;
import com.reservas.plataformareservas.entity.Habitacion;
import com.reservas.plataformareservas.entity.Reserva;
import com.reservas.plataformareservas.entity.Usuario;
import com.reservas.plataformareservas.repository.HabitacionRepository;
import com.reservas.plataformareservas.repository.ReservaRepository;
import com.reservas.plataformareservas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaDto crear(ReservaRequestDto dto, String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);

        if (!dto.getFechaSalida().isAfter(dto.getFechaEntrada())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de salida debe ser posterior a la de entrada");
        }

        Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        if ("MANTENIMIENTO".equals(habitacion.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La habitación está en mantenimiento");
        }

        long cruces = reservaRepository.contarCruces(
                habitacion.getId(), dto.getFechaEntrada(), dto.getFechaSalida());
        if (cruces > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La habitación ya está reservada en esas fechas");
        }

        long noches = ChronoUnit.DAYS.between(dto.getFechaEntrada(), dto.getFechaSalida());
        BigDecimal total = habitacion.getPrecioNoche().multiply(BigDecimal.valueOf(noches));

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFechaEntrada(dto.getFechaEntrada());
        reserva.setFechaSalida(dto.getFechaSalida());
        reserva.setEstado("CONFIRMADA");
        reserva.setTotal(total);

        return aDto(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> misReservas(String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);
        return reservaRepository.findByUsuarioIdOrderByFechaEntradaDesc(usuario.getId())
                .stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaDto> listarTodas() {
        return reservaRepository.findAll().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public ReservaDto obtener(Long id, String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);
        Reserva reserva = buscarReserva(id);
        verificarDueno(reserva, usuario);
        return aDto(reserva);
    }

    public ReservaDto cancelar(Long id, String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);
        Reserva reserva = buscarReserva(id);
        verificarDueno(reserva, usuario);

        if ("CANCELADA".equals(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva ya está cancelada");
        }
        reserva.setEstado("CANCELADA");
        return aDto(reservaRepository.save(reserva));
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
    }

    private void verificarDueno(Reserva reserva, Usuario usuario) {
        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a esta reserva");
        }
    }

    private ReservaDto aDto(Reserva r) {
        return new ReservaDto(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNombre(),
                r.getHabitacion().getId(),
                r.getHabitacion().getNumero(),
                r.getHabitacion().getTipo(),
                r.getFechaEntrada(),
                r.getFechaSalida(),
                r.getEstado(),
                r.getTotal());
    }
}