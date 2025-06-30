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
        String nombreCategoria = payload.get("nombreCategoria");

        Categoria nuevaCategoria = categoriaService.crearCategoria(nombreCategoria);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    //Endpoint para obtener categoria por id
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable Long id){
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    //Enpoint para obtener todas las categorias
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasCategorias(){
        List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }


    //Endpoint para actualizar categoria 
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody Map<String, String> payload){
        String nuevoNombre = payload.get("nuevoNombre");
            if (nuevoNombre == null || nuevoNombre.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nuevo nombre es obligatorio"));

            }
            Categoria categoria = categoriaService.actualizarCategoria(id, nuevoNombre);
            return ResponseEntity.ok(categoria);
    }

    
    //Endpoint para eliminar categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id){
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }


    //Endpoint para buscar una lista de coincidencias 
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCategoriasPorNombre(@RequestParam String nombre){
        List<Categoria> categorias = categoriaService.buscarCategoriasPorNombre(nombre);
        return ResponseEntity.ok(categorias);
    }

}
