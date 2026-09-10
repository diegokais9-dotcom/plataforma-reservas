package com.reservas.plataformareservas.repository;

import com.reservas.plataformareservas.entity.Rol;
import com.reservas.plataformareservas.entity.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolEnum nombre);
}