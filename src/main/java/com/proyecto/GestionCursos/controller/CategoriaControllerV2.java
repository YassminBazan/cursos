package com.proyecto.GestionCursos.controller;

import com.proyecto.GestionCursos.assemblers.CategoriaModelAssembler;
import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/categorias")
@Tag(name = "Categorías", description = "Operaciones CRUD relacionadas con categorías de cursos")
@RequiredArgsConstructor
public class CategoriaControllerV2 {

    private final CategoriaService categoriaService;
    private final CategoriaModelAssembler assembler;

    @Operation(summary = "Crear una nueva categoría", description = "Crea una nueva categoría a partir del nombre proporcionado.")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    // Add RequestBody annotation for Swagger UI documentation
    @RequestBody(
        description = "Objeto JSON que contiene el nombre de la categoría.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "object", example = "{\"nombreCategoria\": \"Nueva Categoría Ejemplo\"}"), // Simplified schema and example
            examples = {
                @ExampleObject(
                    name = "Ejemplo de solicitud para crear categoría",
                    value = "{\"nombreCategoria\": \"Desarrollo Web\"}"
                )
            }
        )
    )
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Categoria>> crearCategoria(@RequestBody Map<String, String> payload) {
        String nombreCategoria = payload.get("nombreCategoria");

        // Explicit validation in the controller
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            // Return a 400 Bad Request with a custom message
            Map<String, String> errorResponse = Map.of("error", "El nombre de la categoría es obligatorio y no puede estar vacío.");
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Categoria nuevaCategoria = categoriaService.crearCategoria(nombreCategoria);
        return ResponseEntity
                .created(linkTo(methodOn(CategoriaControllerV2.class).obtenerCategoriaPorId(nuevaCategoria.getIdCategoria())).toUri())
                .body(assembler.toModel(nuevaCategoria));
    }

    

    @Operation(summary = "Obtener todas las categorías", description = "Devuelve una lista de todas las categorías disponibles.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Categoria>> obtenerTodasLasCategorias() {
        List<EntityModel<Categoria>> categorias = categoriaService.obtenerTodasLasCategorias().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(categorias,
                linkTo(methodOn(CategoriaControllerV2.class).obtenerTodasLasCategorias()).withSelfRel());
    }

    

    @Operation(summary = "Obtener una categoría por ID", description = "Devuelve una categoría específica por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        return assembler.toModel(categoria);
    }

    

    @Operation(summary = "Actualizar el nombre de una categoría", description = "Modifica el nombre de una categoría existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "El nuevo nombre es obligatorio"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Categoria>> actualizarCategoria(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String nuevoNombre = payload.get("nuevoNombre");
        // Ensure you also validate nuevoNombre here if it's mandatory for update
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            Map<String, String> errorResponse = Map.of("error", "El nuevo nombre de la categoría es obligatorio y no puede estar vacío.");
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Categoria categoria = categoriaService.actualizarCategoria(id, nuevoNombre);
        return ResponseEntity.ok(assembler.toModel(categoria));
    }

    
    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    

    @Operation(summary = "Buscar categorías por nombre", description = "Filtra las categorías que coinciden parcial o totalmente con el nombre.")
    @ApiResponse(responseCode = "200", description = "Listado de coincidencias encontrado")
    @GetMapping(value = "/buscar", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Categoria>> buscarCategoriasPorNombre(@RequestParam String nombre) {
        List<EntityModel<Categoria>> resultados = categoriaService.buscarCategoriasPorNombre(nombre).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(resultados,
                linkTo(methodOn(CategoriaControllerV2.class).buscarCategoriasPorNombre(nombre)).withSelfRel());
    }
}