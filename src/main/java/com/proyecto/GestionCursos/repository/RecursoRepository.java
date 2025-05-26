package com.proyecto.GestionCursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.EstadoRecursoEnum;
import com.proyecto.GestionCursos.model.Recurso;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long>{

    //Método para buscar por estado 
    List<Recurso> findByEstado(EstadoRecursoEnum estado);

    //Método para buscar por id
    Optional<Recurso> findByIdRecurso(Long idRecurso);

    //Método para buscar por estado y por Id de curso
    List<Recurso> findByCurso_IdCursoAndEstado(Long idCurso, EstadoRecursoEnum estado);

    //Método que muestre una lista de los recursos asignados por curso
    List<Recurso> findByCurso_IdCurso(Long idCurso);

    //Método para retornar todos los recursos creados por un usuario
    List<Recurso> findByIdUsuario(Long idUsuario);

    //Método para retornar una lista de recursos que tengan coincidencia con lo ingresado
    List<Recurso> findByNombreRecursoContainingIgnoreCase(String nombreRecurso);

}
