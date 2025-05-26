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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.GestionCursos.model.EstadoRecursoEnum;
import com.proyecto.GestionCursos.model.Recurso;
import com.proyecto.GestionCursos.service.RecursoService;

@RestController
@RequestMapping("/api/v1")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService){
        this.recursoService = recursoService;
    }

    //Enpoint para crear un recurso
    @PostMapping("/cursos/{cursoId}/recursos")
    public ResponseEntity<?> crearRecurso(@PathVariable Long cursoId, @RequestBody Recurso recurso, @RequestHeader(name = "X-User-ID", required = true) Long idUsuarioCreador){
        try {
            Recurso nuevoRecurso = recursoService.crearRecurso(recurso, cursoId, idUsuarioCreador);
            return new ResponseEntity<>(nuevoRecurso, HttpStatus.CREATED);
        }catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear recurso: " + e.getMessage());
        }
    }

    //Endpoint para obtener recurso por id
    @GetMapping("/recursos/{id}")
    public ResponseEntity<?> obtenerRecursoPorId(@PathVariable Long id){
        try {
            Recurso recurso = recursoService.obtenerRecursoPorId(id);
            return ResponseEntity.ok(recurso);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al obtener recurso: " + e.getMessage());
        }
    }

    //Endpoint para obtener recursos por curso
    @GetMapping("/cursos/{cursoId}/recursos")
    public ResponseEntity<?> obtenerRecursosPorCurso(@PathVariable Long cursoId){
        try {
            List<Recurso> recursos = recursoService.obtenerRecursosPorCurso(cursoId);
            if (recursos.isEmpty()) {
                return ResponseEntity.ok("No hay recursos disponibles");
            }
            return ResponseEntity.ok(recursos);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al obtener recursos: " + e.getMessage());

        }
    }
    //Endpoint para eliminar un recurso
    @DeleteMapping("/recursos/{id}")
    public ResponseEntity<?> eliminarRecurso(@PathVariable Long id, @RequestHeader(name = "X-User-ID", required = true) Long idUsuarioGerente){
        try {
            recursoService.eliminarRecurso(id, idUsuarioGerente);
            return ResponseEntity.ok("Recurso eliminado correctamente");
        }catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar recurso: " + e.getMessage());
        }
    }

    //Endpoint para aprobar recurso
    @PutMapping("/recursos/{id}/aprobar")
    public ResponseEntity<?> aprobarRecurso(@PathVariable Long id, @RequestHeader(name = "X-User-ID", required = true) Long idUsuarioGerente){
        try {
            Recurso recursoAprobado = recursoService.aprobarRecurso(id, idUsuarioGerente);
            return ResponseEntity.ok(recursoAprobado);
        }catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al aprobar recurso: " + e.getMessage());
        }
    }
    //Enpoint para rechazar recurso
    @PutMapping("/recursos/{id}/rechazar")
    public ResponseEntity<?> rechazarRecurso(@PathVariable Long id, @RequestHeader(name = "X-User-ID", required = true) Long idUsuarioGerente){
        try {
            Recurso recursoRechazado = recursoService.rechazarRecurso(id, idUsuarioGerente);
            return ResponseEntity.ok(recursoRechazado);

        }catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al rechazar recurso: " + e.getMessage());
        }
    }

    //Endpoint para buscar recurso por curso y estado
    @GetMapping("/cursos/{cursoId}/recursos/estado")
    public ResponseEntity<?> buscarRecursoPorCursoYEstado(@PathVariable Long cursoId, @RequestParam EstadoRecursoEnum estado){
        try {
            List<Recurso> recursos = recursoService.buscarRecursosPorCursoYEstado(cursoId, estado);
            if (recursos.isEmpty()) {
                return ResponseEntity.ok("No se encontraron recursos");
            }
            return ResponseEntity.ok(recursos);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al buscar recursos: " + e.getMessage());
        }
    }



}
