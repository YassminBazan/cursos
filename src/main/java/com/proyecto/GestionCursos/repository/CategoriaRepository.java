package com.proyecto.GestionCursos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.GestionCursos.model.Categoria;
import java.util.List;


@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    //Método para buscar categoria por id
    Optional<Categoria> findById(Long idCategoria);

    boolean existsById(Long idCategoria);

    //Método para buscar una categoria por el nombre 
    Optional<Categoria> findByNombreCategoriaIgnoreCase(String nombreCategoria);

    //Método que devuelve una lista de las categorias que tengan coincidencia con el nombre ingresado
    List<Categoria> findByNombreCategoriaContainingIgnoreCase(String nombreCategoria);


}
