package com.proyecto.GestionCursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>{


    //Devuelve una lista de los cursos que coincidan con el nombre que ingreso (no es key sensitive)
    List<Curso> findByNombreCursoContainingIgnoreCase(String nombreCurso);

    //Devuelve una lista de cursos que corresponden a una categoria, se indica a JPA que navegue en la lista categorias y acceda a nombreCategoria 
    List<Curso> findByCategorias_NombreCategoriaIgnoreCase(String nombreCategoria);

    //Devuelve los cursos asignados a un instructor, contains es para buscar si el instructorId esta dentro de la coleccion instructorIds
    List<Curso> findByInstructorIdsContains(Long instructorId);

    //Devuelve la lista de los cursos a cargo de los gerentes
    List<Curso> findByIdUsuario(Long idUsuario);

    //Método para buscar un curso especifico, Optional indica que puede o no existir el objeto
    Optional<Curso> findByNombreCursoIgnoreCase(String nombreCurso);

    //Devuelve un curso por id
    Optional<Curso> findByIdCurso(Long idCurso);

    //Métodos exists
    //Nombre ignorando minúsculas y mayúsculas
    boolean existsByNombreCursoIgnoreCase(String nombreCurso);

    //ID
    boolean existsByIdCurso(Long idCurso);

    List<Curso> findByCategorias_IdCategoria(Long categoriaId);


}
