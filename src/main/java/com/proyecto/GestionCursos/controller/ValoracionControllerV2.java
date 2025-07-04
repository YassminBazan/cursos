package com.proyecto.GestionCursos.controller;

import com.proyecto.GestionCursos.assemblers.ValoracionModelAssembler;
import com.proyecto.GestionCursos.model.Valoracion;
import com.proyecto.GestionCursos.service.ValoracionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/valoraciones")
@Tag(name = "Valoraciones", description = "Operaciones relacionadas con valoraciones de cursos")
@RequiredArgsConstructor
public class ValoracionControllerV2 {

    private final ValoracionService valoracionService;
    private final ValoracionModelAssembler assembler;

    @Operation(summary = "Crear una nueva valoración", description = "Crea una valoración para un curso")
    @ApiResponse(responseCode = "201", description = "Valoración creada exitosamente")
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Valoracion>> crearValoracion(@RequestBody Map<String, Object> payload) {
        Long idUsuario = ((Number) payload.get("idUsuario")).longValue();
        Long idCurso = ((Number) payload.get("idCurso")).longValue();
        Integer puntuacion = (Integer) payload.get("puntuacion");
        String comentario = (String) payload.get("comentario");

        Valoracion valoracionCreada = valoracionService.crearValoracion(idUsuario, idCurso, puntuacion, comentario);

        return ResponseEntity
                .created(linkTo(methodOn(ValoracionControllerV2.class).obtenerValoracionPorId(valoracionCreada.getIdValoracion())).toUri())
                .body(assembler.toModel(valoracionCreada));
    }

    @Operation(summary = "Actualizar una valoración", description = "Actualiza una valoración existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Valoración actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Valoración no encontrada")
    })
    @PutMapping(value = "/{idValoracion}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Valoracion>> actualizarValoracion(@PathVariable Long idValoracion,
                                                                       @RequestBody Map<String, Object> payload) {
        Long idUsuarioQueActualiza = ((Number) payload.get("idUsuario")).longValue();
        Integer puntuacion = (Integer) payload.get("puntuacion");
        String comentario = (String) payload.get("comentario");

        Optional<Valoracion> valoracionActualizadaOpt = valoracionService.actualizarValoracion(idValoracion, puntuacion, comentario, idUsuarioQueActualiza);

        return valoracionActualizadaOpt
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    @Operation(summary = "Obtener una valoración por ID", description = "Obtiene una valoración específica por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Valoración encontrada"),
        @ApiResponse(responseCode = "404", description = "Valoración no encontrada")
    })
    @GetMapping(value = "/{idValoracion}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Valoracion>> obtenerValoracionPorId(@PathVariable Long idValoracion) {
        Optional<Valoracion> valoracionOpt = valoracionService.obtenerValoracionPorId(idValoracion);
        return valoracionOpt
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener valoraciones por usuario", description = "Obtiene todas las valoraciones realizadas por un usuario")
    @GetMapping(value = "/usuario/{idUsuario}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Valoracion>> obtenerValoracionesPorUsuario(@PathVariable Long idUsuario) {
        List<EntityModel<Valoracion>> valoraciones = valoracionService.obtenerValoracionPorUsuario(idUsuario).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(valoraciones,
                linkTo(methodOn(ValoracionControllerV2.class).obtenerValoracionesPorUsuario(idUsuario)).withSelfRel());
    }

    @Operation(summary = "Obtener valoraciones por curso", description = "Obtiene todas las valoraciones de un curso específico")
    @GetMapping(value = "/curso/{idCurso}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Valoracion>> obtenerValoracionesPorCurso(@PathVariable Long idCurso) {
        List<EntityModel<Valoracion>> valoraciones = valoracionService.obtenerValoracionPorCurso(idCurso).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(valoraciones,
                linkTo(methodOn(ValoracionControllerV2.class).obtenerValoracionesPorCurso(idCurso)).withSelfRel());
    }

    @Operation(summary = "Obtener todas las valoraciones", description = "Obtiene todas las valoraciones registradas")
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Valoracion>> obtenerTodasLasValoraciones() {
        List<EntityModel<Valoracion>> valoraciones = valoracionService.obtenerTodasLasValoraciones().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(valoraciones,
                linkTo(methodOn(ValoracionControllerV2.class).obtenerTodasLasValoraciones()).withSelfRel());
    }
}
