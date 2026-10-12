package com.reservas.plataformareservas.repository;

import com.reservas.plataformareservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioIdOrderByFechaEntradaDesc(Long usuarioId);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.habitacion.id = :habitacionId "
            + "AND r.estado = 'CONFIRMADA' "
            + "AND r.fechaEntrada < :salida AND r.fechaSalida > :entrada")
    long contarCruces(@Param("habitacionId") Long habitacionId,
                      @Param("entrada") LocalDate entrada,
                      @Param("salida") LocalDate salida);
}