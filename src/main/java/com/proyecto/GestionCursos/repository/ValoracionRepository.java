package com.proyecto.GestionCursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.Valoracion;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    //Devuelve una valoracion por id
    Optional<Valoracion> findByIdValoracion(Long idValoracion);

    //Devuelve una lista de valoraciones de un usuario
    List<Valoracion> findByIdUsuario(Long idUsuario);

    //Devuelve una lista de valoraciones por curso
    List<Valoracion> findByIdCurso(Long idCurso);

    

}
