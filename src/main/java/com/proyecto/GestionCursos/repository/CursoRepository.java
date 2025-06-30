package com.proyecto.GestionCursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.Curso;


@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    //Devuelve curso por id
    Optional<Curso> findById(Long idCurso);

    //Devuelve curso por nombre 
    Optional<Curso> findByNombreCurso(String nombreCurso);


    //Devuelve una lista de cursos de una categoria 
    List<Curso> findByCategorias_IdCategoria(Long categoriaId);


    //Devuelve una lista de cursos por categoria
    //List<Curso> findByCategoria(String nombreCategoria);


    //@Query("SELECT c FROM Curso c JOIN c.categorias cat WHERE cat.nombre_categoria = :nombreCategoria")
    //List<Curso> findByNombreCategoria(@Param("nombreCategoria") String nombreCategoria);


}
