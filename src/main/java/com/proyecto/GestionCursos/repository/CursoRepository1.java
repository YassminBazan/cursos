package com.proyecto.GestionCursos.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.Curso;


@Repository
public interface CursoRepository1 {

    //Devuelve curso por id
    Optional<Curso> findByIdCurso(Long idCurso);

    //Devuelve 

}
