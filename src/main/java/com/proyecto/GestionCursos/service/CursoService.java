package com.proyecto.GestionCursos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.repository.CategoriaRepository;
import com.proyecto.GestionCursos.repository.CursoRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final CategoriaRepository categoriaRepository;

    
    public CursoService(CursoRepository cursoRepository, CategoriaRepository categoriaRepository){
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    //Para registrar un nuevo curso
    @Transactional //Indica que los métodos se van hacer dentro de una transaccion para garantizar la integridad de datos 
    public Curso crearCurso(Curso curso, Long idCreador){
        if (!usuarioRolService.tieneRol(idCreador, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("Usuario ingresado no tiene permiso para crear cursos"); 
        }

        if (cursoRepository.existsByNombreCursoIgnoreCase(curso.getNombreCurso())) {
            throw new IllegalArgumentException("El nombre " + curso.getNombreCurso() + "ya existe");  
        }
        
    //Verificacion existencia de categoria
        if (curso.getCategorias() != null) {
            for(Categoria categoria : curso.getCategorias()){
                if (categoria.getIdCategoria() == null || !categoriaRepository.existsById(categoria.getIdCategoria())) {
                    throw new IllegalArgumentException("Categoria no existe");
                    
                }
            }
        }

    //Verificacion de roles para los Ids de instructores
        if(curso.getInstructorIds() != null){
            for(Long instructorId : curso.getInstructorIds()){
                if (!usuarioRolService.tieneRol(instructorId, RolEnum.INSTRUCTOR)) {
                    throw new IllegalArgumentException("El usuario asignado a curso no tiene permisos");
            }
        }
    }
        curso.setIdUsuario(idCreador);
        
        return cursoRepository.save(curso);
    }




    @Transactional(readOnly = true)
    public List<Curso> obtenerTodosLosCursos(){
        return cursoRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public Curso obtenerCursoPorId(Long id) {
    return cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado")); 
    }

    //Actualizar cursos
    @Transactional
    public Curso actualizarCurso(Long id, Curso cursoActualizado, Long idUsuario){
        if (!usuarioRolService.tieneRol(idUsuario, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permiso para actualizar cursos");
        }

        Curso cursoExistente = cursoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("El ID no se encuentra"));

        if (!cursoExistente.getNombreCurso().equalsIgnoreCase(cursoActualizado.getNombreCurso()) &&
        cursoRepository.existsByNombreCursoIgnoreCase(cursoActualizado.getNombreCurso())){
            throw new IllegalArgumentException("El nombre del curso ya existe");
        }

        cursoExistente.setNombreCurso(cursoActualizado.getNombreCurso());
        cursoExistente.setDescripcionCurso(cursoActualizado.getDescripcionCurso());
        cursoExistente.setValorCurso(cursoActualizado.getValorCurso());

        //Para actualizar categoria
        if (cursoActualizado.getCategorias() != null) {
            for(Categoria nuevaCategoria : cursoActualizado.getCategorias()){
                if (nuevaCategoria.getIdCategoria() == null || !categoriaRepository.existsById(nuevaCategoria.getIdCategoria())) {
                    throw new IllegalArgumentException("La categoria ingresada no es valida");
                }
            }
            cursoExistente.setCategorias(cursoActualizado.getCategorias());
        }

        return cursoRepository.save(cursoExistente);
    }


    @Transactional
    public void eliminarCurso(Long id, Long idUsuario){
        if (!usuarioRolService.tieneRol(idUsuario, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permiso para eliminar cursos");
        }

        if (!cursoRepository.existsById(id)) {
            throw new RuntimeException("Curso no encontrado");
        }
        cursoRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    public List<Curso> buscarCursoPorNombre(String nombre){
        return cursoRepository.findByNombreCursoContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Curso> buscarPorCategoria(String nombreCategoria){
        return cursoRepository.findByCategorias_NombreCategoriaIgnoreCase(nombreCategoria);
    }

    @Transactional(readOnly = true)
    public List<Curso> buscarCursoPorInstructor(Long instructorId){
        return cursoRepository.findByInstructorIdsContains(instructorId);
    }

    @Transactional(readOnly = true)
    public List<Curso> buscarCursosPorCreador(Long creadorId){
        return cursoRepository.findByIdUsuario(creadorId);
    }


    //Asignacion de instructor
    @Transactional
    public Curso asignarInstructor(Long cursoId, Long instructorId, Long idUsuario){
        if (!usuarioRolService.tieneRol(idUsuario, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permiso para asignar instructores");
        }
        if(!usuarioRolService.tieneRol(instructorId, RolEnum.INSTRUCTOR)){
            throw new IllegalArgumentException("El usuario asignado no cumple con requisitos");
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            
        curso.addInstructor(instructorId);
        return cursoRepository.save(curso);
    }

    //Desvincular instructor
    @Transactional
    public Curso desvincularInstructor(Long cursoId, Long instructorId, Long idUsuario){
        if (!usuarioRolService.tieneRol(idUsuario, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permiso para desvincular instructores");
        }
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        curso.removeInstructor(instructorId);
        return cursoRepository.save(curso);
    }

}
