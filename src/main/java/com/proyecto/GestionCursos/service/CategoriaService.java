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
    public Categoria crearCategoria(Categoria categoria){
        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombreCategoriaIgnoreCase(categoria.getNombreCategoria());
        if (categoriaExistente.isPresent()) {
            throw new IllegalArgumentException("El nombre de la categoria ya existe");    
        }
        return categoriaRepository.save(categoria);
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
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada){
        Categoria categoriaExistente = obtenerCategoriaPorId(id);

        Optional<Categoria> otraCategoria = categoriaRepository.findByNombreCategoriaIgnoreCase(categoriaActualizada.getNombreCategoria());
        if (otraCategoria.isPresent() && !otraCategoria.get().getIdCategoria().equals(id)) {
            throw new IllegalArgumentException("El nombre de la categoria ya existe");
        }
        categoriaExistente.setNombreCategoria(categoriaActualizada.getNombreCategoria());
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
