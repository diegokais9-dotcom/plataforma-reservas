package com.reservas.plataformareservas.repository;

import com.reservas.plataformareservas.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, Long id);

    @Query("SELECT h FROM Habitacion h WHERE h.estado = 'DISPONIBLE' AND h.capacidad >= :capacidad ORDER BY h.precioNoche")
    List<Habitacion> buscarDisponiblesPorCapacidad(@Param("capacidad") Integer capacidad);
}