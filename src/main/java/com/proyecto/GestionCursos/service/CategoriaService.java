package com.proyecto.GestionCursos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.repository.CategoriaRepository;
import com.proyecto.GestionCursos.repository.CursoRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CursoRepository cursoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, CursoRepository cursoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.cursoRepository = cursoRepository;
    }

    //Crear categoria
    @Transactional
    public Categoria crearCategoria(String nombreCategoria){
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoria es obligatorio");
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombreCategoriaIgnoreCase(nombreCategoria);
        if (categoriaExistente.isPresent()) {
            throw new IllegalArgumentException("El nombre de la categoria ya existe");    
        }

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombreCategoria(nombreCategoria);
        return categoriaRepository.save(nuevaCategoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodasLasCategorias(){
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria obtenerCategoriaPorId(Long id){
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));      
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarCategoriaPorNombre(String nombreCategoria){
        return categoriaRepository.findByNombreCategoriaIgnoreCase(nombreCategoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> buscarCategoriasPorNombre(String nombreCategoria){
        return categoriaRepository.findByNombreCategoriaContainingIgnoreCase(nombreCategoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Long id, String nuevoNombre){

        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacio");
        }

        Categoria categoriaExistente = categoriaRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("El id ingresado no coincide con ninguna categoria"));

        Optional<Categoria> otraCategoria = categoriaRepository.findByNombreCategoriaIgnoreCase(nuevoNombre);

        if (otraCategoria.isPresent() && !otraCategoria.get().getIdCategoria().equals(id)) {
            throw new IllegalArgumentException("El nombre de la categoria ya existe");
        }
        categoriaExistente.setNombreCategoria(nuevoNombre);
        return categoriaRepository.save(categoriaExistente);   
    }

    @Transactional
    public void eliminarCategoria(Long categoriaId){
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        List<Curso> cursosAsociados = cursoRepository.findByCategorias_IdCategoria(categoriaId);

        if (cursosAsociados != null && !cursosAsociados.isEmpty()) {
            for(Curso curso : cursosAsociados){
                boolean removed = curso.getCategorias().removeIf(cat -> cat.getIdCategoria().equals(categoriaId));
                
                if(removed){
                    cursoRepository.save(curso);
                }
            }    
        }
        categoriaRepository.delete(categoria);
    }
}
