package com.proyecto.GestionCursos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.service.CategoriaService;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    //Endpoint para crear categoria
    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody Map<String, String> payload){
        try {
            String nombreCategoria = payload.get("nombreCategoria");

            Categoria nuevaCategoria = categoriaService.crearCategoria(nombreCategoria);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear categoria: " + e.getMessage());
        }
    }

    //Endpoint para obtener categoria por id
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable Long id){
        try {
            Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(categoria);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al obtener categoria: " + e.getMessage());
        }
    }

    //Enpoint para obtener todas las categorias
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasCategorias(){
        try {
            List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
            return ResponseEntity.ok(categorias);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al obtener la lista de categorias: " + e.getMessage());
        }
    }
    //Endpoint para actualizar categoria 
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody Map<String, String> payload){
        try {
            String nuevoNombre = payload.get("nuevoNombre");
            if (nuevoNombre == null || nuevoNombre.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nuevo nombre es obligatorio"));

            }
            Categoria categoria = categoriaService.actualizarCategoria(id, nuevoNombre);
            return ResponseEntity.ok(categoria);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al actualizar la categoria: " + e.getMessage());
        }

    }

    //Endpoint para eliminar categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id){
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar categoria: " + e.getMessage());
        }
    }

    //Endpoint para buscar una lista de coincidencias 
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCategoriasPorNombre(@RequestParam String nombre){
        try {
            List<Categoria> categorias = categoriaService.buscarCategoriasPorNombre(nombre);
            return ResponseEntity.ok(categorias);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al buscar categorias: " + e.getMessage());
        }
    }

}
