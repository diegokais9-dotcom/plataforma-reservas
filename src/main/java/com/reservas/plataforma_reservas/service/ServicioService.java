package com.reservas.plataformareservas.service;

import com.reservas.plataformareservas.dto.ServicioDto;
import com.reservas.plataformareservas.entity.Servicio;
import com.reservas.plataformareservas.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {

    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<ServicioDto> listar() {
        return servicioRepository.findAll().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public ServicioDto obtener(Long id) {
        return aDto(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<ServicioDto> buscarPorNombre(String nombre) {
        return servicioRepository.buscarPorNombre(nombre).stream().map(this::aDto).toList();
    }

    public ServicioDto crear(ServicioDto dto) {
        if (servicioRepository.existsByNombre(dto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un servicio con ese nombre");
        }
        Servicio servicio = new Servicio();
        copiarDatos(dto, servicio);
        return aDto(servicioRepository.save(servicio));
    }

    public ServicioDto actualizar(Long id, ServicioDto dto) {
        Servicio servicio = buscarPorId(id);
        if (servicioRepository.existsByNombreAndIdNot(dto.getNombre(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro servicio con ese nombre");
        }
        copiarDatos(dto, servicio);
        return aDto(servicioRepository.save(servicio));
    }

    public void eliminar(Long id) {
        servicioRepository.delete(buscarPorId(id));
    }

    private Servicio buscarPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    private void copiarDatos(ServicioDto dto, Servicio servicio) {
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
    }

    private ServicioDto aDto(Servicio s) {
        return new ServicioDto(s.getId(), s.getNombre(), s.getDescripcion(), s.getPrecio(), s.getActivo());
    }
}