package com.proyecto.GestionCursos.controller;

import com.proyecto.GestionCursos.assemblers.CursoModelAssembler;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/cursos")
@Tag(name = "Cursos", description = "Operaciones CRUD relacionadas con cursos")
@RequiredArgsConstructor
public class CursoControllerV2 {

    private final CursoService cursoService;
    private final CursoModelAssembler assembler;

    @Operation(summary = "Obtener todos los cursos", description = "Devuelve una lista de todos los cursos disponibles.")
    @ApiResponse(responseCode = "200", description = "Listado de cursos obtenido correctamente")
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Curso>> obtenerTodosLosCursos() {
        List<EntityModel<Curso>> cursos = cursoService.obtenerTodosLosCursos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(cursos,
                linkTo(methodOn(CursoControllerV2.class).obtenerTodosLosCursos()).withSelfRel());
    }

    @Operation(summary = "Obtener un curso por ID", description = "Devuelve un curso específico dado su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping(value = "/{idCurso}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Curso>> obtenerCursoPorId(@PathVariable Long idCurso) {
        return cursoService.obtenerCursoPorId(idCurso)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo curso", description = "Crea un nuevo curso con los datos proporcionados.")
    @ApiResponse(responseCode = "201", description = "Curso creado exitosamente")
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Curso>> crearCurso(@RequestBody Map<String, Object> payload) {

        String nombre = (String) payload.get("nombreCurso");
        String descripcion = (String) payload.get("descripcion");
        double valor = ((Number) payload.get("valorCurso")).doubleValue();
        Long idCreador = ((Number) payload.get("idCreador")).longValue();

        List<Integer> idsCategoriasInt = (List<Integer>) payload.get("idsCategorias");
        Set<Long> idsCategorias = idsCategoriasInt.stream().map(Integer::longValue).collect(Collectors.toSet());

        Curso cursoCreado = cursoService.crearCurso(nombre, descripcion, valor, idCreador, idsCategorias);

        return ResponseEntity
                .created(linkTo(methodOn(CursoControllerV2.class).obtenerCursoPorId(cursoCreado.getIdCurso())).toUri())
                .body(assembler.toModel(cursoCreado));
    }

    @Operation(summary = "Actualizar un curso", description = "Actualiza los datos de un curso existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping(value = "/{idCurso}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Curso>> actualizarCurso(@PathVariable Long idCurso, @RequestBody Map<String, Object> payload) {

        String nombre = (String) payload.get("nombreCurso");
        String descripcion = (String) payload.get("descripcion");
        double valor = ((Number) payload.get("valorCurso")).doubleValue();

        return cursoService.actualizarCurso(idCurso, nombre, descripcion, valor)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }

    @Operation(summary = "Eliminar un curso", description = "Elimina un curso dado su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @DeleteMapping(value = "/{idCurso}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> eliminarCurso(@PathVariable Long idCurso) {
        cursoService.eliminarCurso(idCurso);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Asignar instructor a un curso", description = "Asocia un instructor a un curso dado.")
    @ApiResponse(responseCode = "200", description = "Instructor asignado correctamente")
    @PostMapping(value = "/{idCurso}/asignar", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Curso>> asignarInstructor(@PathVariable Long idCurso, @RequestBody Map<String, Long> payload) {
        Long idInstructor = payload.get("idInstructor");
        Curso cursoActualizado = cursoService.asignarInstructor(idCurso, idInstructor);
        return ResponseEntity.ok(assembler.toModel(cursoActualizado));
    }

    @Operation(summary = "Desvincular instructor de un curso", description = "Remueve un instructor de un curso dado.")
    @ApiResponse(responseCode = "200", description = "Instructor desvinculado correctamente")
    @DeleteMapping(value = "/{idCurso}/desvincular/{idInstructor}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Curso>> desvincularInstructor(@PathVariable Long idCurso, @PathVariable Long idInstructor) {
        Curso cursoActualizado = cursoService.desvincularInstructor(idCurso, idInstructor);
        return ResponseEntity.ok(assembler.toModel(cursoActualizado));
    }
}
