package com.proyecto.GestionCursos.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.service.CursoService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {

    private final CursoService cursoService;

    //Endpoint para CREAR CURSO
    @PostMapping
    public ResponseEntity<?> crearCurso(@RequestBody Map<String, Object> payload){
        
        String nombre = (String) payload.get("nombreCurso");
        String descripcion = (String) payload.get("descripcion");
        double valor = ((Number) payload.get("valorCurso")).doubleValue();
        Long idCreador = ((Number) payload.get("idUsuarioCreador")).longValue();
            
        List<Integer> idsCategoriasInt = (List<Integer>) payload.get("idsCategorias");
        Set<Long> idsCategorias = idsCategoriasInt.stream().map(Integer::longValue).collect(Collectors.toSet());

        Curso cursoCreado = cursoService.crearCurso(nombre, descripcion, valor, idCreador, idsCategorias);
        return new ResponseEntity<>(cursoCreado, HttpStatus.CREATED);
    }

    //Endpoint para LISTAR TODOS LOS CURSOS
    @GetMapping
    public ResponseEntity<List<Curso>> obtenerTodosLosCursos(){
        List<Curso> cursos = cursoService.obtenerTodosLosCursos();
        return ResponseEntity.ok(cursos);
    }

    //Endpoint para LISTAR CURSO POR ID
    @GetMapping("/{idCurso}")
    public ResponseEntity<Curso> obtenerCursoPorId(@PathVariable Long idCurso){
        return cursoService.obtenerCursoPorId(idCurso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Endpoint para ACTUALIZAR CURSO
    @PutMapping("/{idCurso}")
    public ResponseEntity<?> actualizarCurso(@PathVariable Long idCurso, @RequestBody Map<String, Object> payload){
        String nombre = (String) payload.get("nombreCurso");
        String descripcion = (String) payload.get("descripcion");
        double valor = ((Number) payload.get("valorCurso")).doubleValue();

        return cursoService.actualizarCurso(idCurso, nombre, descripcion, valor)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Curso no encontrado con ID: " + idCurso)));
    }

    //Endpoint para ELIMINAR CURSO
    @DeleteMapping("/{idCurso}")
    public ResponseEntity<?> eliminarCurso(@PathVariable Long idCurso){
        cursoService.eliminarCurso(idCurso);
        return ResponseEntity.noContent().build();
    }

    //Endpoint para ASIGNAR INSTRUCTOR
    @PostMapping("/{idCurso}/instructores")
    public ResponseEntity<?> asignarInstructor(@PathVariable Long idCurso, @RequestBody Map<String, Long> payload){
        Long idInstructor = payload.get("idInstructor");
        Curso cursoActualizado = cursoService.asignarInstructor(idCurso, idInstructor);
        return ResponseEntity.ok(cursoActualizado);
    }

    //Endpoint para DESVINCULAR INSTRUCTOR
    @DeleteMapping("/{idCurso}/instructores/{idInstructor}")
    public ResponseEntity<?> desvincularInstructor(@PathVariable Long idCurso, @PathVariable Long idInstructor){
        Curso cursoActualizado = cursoService.desvincularInstructor(idCurso, idInstructor);
        return ResponseEntity.ok(cursoActualizado);
    }


}
