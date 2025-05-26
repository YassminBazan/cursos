package com.proyecto.GestionCursos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.service.CursoService;

@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService){
        this.cursoService = cursoService;
    }

    //Enpoint para crear un curso
    @PostMapping("/registro")
    public ResponseEntity<?> crearCurso(@RequestBody Curso curso, @RequestHeader(name = "X-User-ID", required = true) Long idCreador){
        //RequesHeader es para obtener el id del usuario que reliza la accion
        try {
            Curso nuevoCurso = cursoService.crearCurso(curso, idCreador);
            return new ResponseEntity<>(nuevoCurso, HttpStatus.CREATED);

        }catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear curso: " + e.getMessage());
        } 
    }

    //Enpoint para obtener un curso por id
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCursoPorId(@PathVariable Long id){
        try {
            Curso curso = cursoService.obtenerCursoPorId(id);
            return ResponseEntity.ok(curso);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al obtener el curso: " + e.getMessage());
        }
    }

    //Endpoint para obtener todos los cursos
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosCursos(){
        try {
            List<Curso> cursos = cursoService.obtenerTodosLosCursos();
            if (cursos.isEmpty()) {
                return ResponseEntity.ok("No hay cursos registrados.");
            }
            return ResponseEntity.ok(cursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al obtener la lista de cursos: " + e.getMessage());
        }
    }
    //Enpoint para actualizar un curso
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCurso(@PathVariable Long id, @RequestBody Curso cursoActualizado,  @RequestHeader(name = "X-User-ID", required = true) Long idUsuario){
        try {
            Curso curso = cursoService.actualizarCurso(id, cursoActualizado, idUsuario);
            return ResponseEntity.ok(curso);
        }catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        }  catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }  catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al actualizar curso: " + e.getMessage());
        }
    }

    //Endpoint para eliminar un curso por id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCurso(@PathVariable Long id, @RequestHeader(name = "X-User-ID", required = true) Long idUsuario){
        try {
            cursoService.eliminarCurso(id, idUsuario);
            return ResponseEntity.ok("Curso eliminado con éxito");
        }catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar curso: " + e.getMessage());
        }
    }

}
