package com.reservas.plataformareservas.service;

import com.reservas.plataformareservas.dto.HabitacionDto;
import com.reservas.plataformareservas.entity.Habitacion;
import com.reservas.plataformareservas.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    @Transactional(readOnly = true)
    public List<HabitacionDto> listar() {
        return habitacionRepository.findAll().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public HabitacionDto obtener(Long id) {
        return aDto(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<HabitacionDto> listarDisponibles(Integer capacidad) {
        return habitacionRepository.buscarDisponiblesPorCapacidad(capacidad).stream().map(this::aDto).toList();
    }

    public HabitacionDto crear(HabitacionDto dto) {
        if (habitacionRepository.existsByNumero(dto.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una habitación con ese número");
        }
        Habitacion habitacion = new Habitacion();
        copiarDatos(dto, habitacion);
        return aDto(habitacionRepository.save(habitacion));
    }

    public HabitacionDto actualizar(Long id, HabitacionDto dto) {
        Habitacion habitacion = buscarPorId(id);
        if (habitacionRepository.existsByNumeroAndIdNot(dto.getNumero(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otra habitación con ese número");
        }
        copiarDatos(dto, habitacion);
        return aDto(habitacionRepository.save(habitacion));
    }

    public void eliminar(Long id) {
        habitacionRepository.delete(buscarPorId(id));
    }

    private Habitacion buscarPorId(Long id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));
    }

    private void copiarDatos(HabitacionDto dto, Habitacion habitacion) {
        habitacion.setNumero(dto.getNumero());
        habitacion.setTipo(dto.getTipo());
        habitacion.setCapacidad(dto.getCapacidad());
        habitacion.setPrecioNoche(dto.getPrecioNoche());
        habitacion.setEstado(dto.getEstado() != null ? dto.getEstado() : "DISPONIBLE");
    }

    private HabitacionDto aDto(Habitacion h) {
        return new HabitacionDto(h.getId(), h.getNumero(), h.getTipo(), h.getCapacidad(), h.getPrecioNoche(), h.getEstado());
    }
}