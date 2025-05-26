package com.proyecto.GestionCursos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.model.EstadoRecursoEnum;
import com.proyecto.GestionCursos.model.Recurso;
import com.proyecto.GestionCursos.model.RolEnum;
import com.proyecto.GestionCursos.repository.CursoRepository;
import com.proyecto.GestionCursos.repository.RecursoRepository;
import org.springframework.transaction.annotation.Transactional;



@Service
public class RecursoService {


    private final RecursoRepository recursoRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRolService usuarioRolService;

    public RecursoService(RecursoRepository recursoRepository, CursoRepository cursoRepository, UsuarioRolService usuarioRolService){
        this.recursoRepository = recursoRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRolService = usuarioRolService;
    }

    //Creacion de recurso asociado a un curso existente    
    @Transactional
    public Recurso crearRecurso(Recurso recurso, Long cursoId, Long idUsuarioCreador){
        if (!usuarioRolService.tieneRol(idUsuarioCreador, RolEnum.INSTRUCTOR)) {
            throw new SecurityException("El usuario ingresado no tiene permisos para crear recursos");
        }

        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        recurso.setCurso(curso);
        recurso.setIdUsuario(idUsuarioCreador);

        return recursoRepository.save(recurso);
    }

    @Transactional(readOnly = true)
    public Recurso obtenerRecursoPorId(Long id){
        return recursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Recurso> obtenerRecursosPorCurso(Long cursoId){
        if (!cursoRepository.existsById(cursoId)) {
            throw new RuntimeException("Curso no encontrado");
        }
        return recursoRepository.findByCurso_IdCurso(cursoId);
    }

    //Aprobar recurso
    @Transactional
    public Recurso aprobarRecurso(Long recursoId, Long idUsuarioGerente){
        if (!usuarioRolService.tieneRol(idUsuarioGerente, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permisos para aprobar recursos");
        }
        Recurso recurso = obtenerRecursoPorId(recursoId);
        recurso.setEstado(EstadoRecursoEnum.APROBADO);
        return recursoRepository.save(recurso);
    }

    //Rechazar recurso
    @Transactional
    public Recurso rechazarRecurso(Long recursoId, Long idUsuarioGerente){
        if (!usuarioRolService.tieneRol(idUsuarioGerente, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permisos para rechazar recursos");
        }

        Recurso recurso = obtenerRecursoPorId(recursoId);
        recurso.setEstado(EstadoRecursoEnum.RECHAZADO);
        return recursoRepository.save(recurso);
    }

    //Buscar recurso por estado
    @Transactional(readOnly = true)
    public List<Recurso> buscarRecursosPorCursoYEstado(Long cursoId, EstadoRecursoEnum estado){
        if (!cursoRepository.existsById(cursoId)) {
            throw new RuntimeException("Curso no encontrado");
        }
        return recursoRepository.findByCurso_IdCursoAndEstado(cursoId, estado);
    }

    @Transactional(readOnly = true)
    public List<Recurso> buscarRecursoPorCreador(Long idUsuario){
        return recursoRepository.findByIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Recurso> buscarRecursoPorNombre(String nombreRecurso){
        return recursoRepository.findByNombreRecursoContainingIgnoreCase(nombreRecurso);
    }

    //  Eliminar recurso
    @Transactional
    public void eliminarRecurso(Long id, Long idUsuarioGerente){
        if (!usuarioRolService.tieneRol(idUsuarioGerente, RolEnum.GERENTE_DE_CURSOS)) {
            throw new SecurityException("El usuario no tiene permisos para eliminar recursos");
        }        
        if (!recursoRepository.existsById(id)) {
            throw new RuntimeException("Recurso no encontrado");
        }
        recursoRepository.deleteById(id);
    }
}
