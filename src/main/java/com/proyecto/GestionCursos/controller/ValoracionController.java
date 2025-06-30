package com.proyecto.GestionCursos.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.GestionCursos.model.Valoracion;
import com.proyecto.GestionCursos.service.ValoracionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;

    //Endpoint para crear
    @PostMapping
    public ResponseEntity<?> crearValoracion(@RequestBody Map<String, Object> payload){
        
            Long idCreador = ((Number) payload.get("idUsuario")).longValue();
            
            // Extraemos los otros datos como antes
            Long idCurso = ((Number) payload.get("idCurso")).longValue();
            Integer puntuacion = (Integer) payload.get("puntuacion");
            String comentario = (String) payload.get("comentario");

            // El servicio ya espera el idCreador como parámetro, así que la llamada no cambia
            Valoracion valoracionCreada = valoracionService.crearValoracion(idCreador, idCurso, puntuacion, comentario);

            return new ResponseEntity<>(valoracionCreada, HttpStatus.CREATED);
    }

    //Endpoint para actualizar
    @PutMapping("/{idValoracion}")
    public ResponseEntity<?> actualizarValoracion(@PathVariable Long idValoracion,  @RequestBody Map<String, Object> payload){

     Long idUsuarioQueActualiza = ((Number) payload.get("idUsuario")).longValue();

            Integer puntuacion = (Integer) payload.get("puntuacion");
            String comentario = (String) payload.get("comentario");

            Optional<Valoracion> valoracionActualizadaOpt = valoracionService.actualizarValoracion(idValoracion, puntuacion, comentario, idUsuarioQueActualiza);
            return valoracionActualizadaOpt
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Valoración no encontrada.")));
    }

    //Endpoint para obtener valoracion por id
    @GetMapping("/{idValoracion}")
    public ResponseEntity<Valoracion> obtenerValoracionPorId(@PathVariable Long idValoracion){
        Optional<Valoracion> valoracionOpt = valoracionService.obtenerValoracionPorId(idValoracion);
        
        return valoracionOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Enpoint para obtener todas las valoraciones de un usuario 
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Valoracion>> obtenerValoracionesPorUsuario(@PathVariable Long idUsuario){
        List<Valoracion> valoraciones = valoracionService.obtenerValoracionPorUsuario(idUsuario);
        return ResponseEntity.ok(valoraciones);
    }

    //Endpoiny para obtener todas las valoraciones de un curso
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<Valoracion>> obtenerValoracionesPorCurso(@PathVariable Long idCurso){
        List<Valoracion> valoraciones = valoracionService.obtenerValoracionPorCurso(idCurso);
        return ResponseEntity.ok(valoraciones);
    }

    //Endpoiny para obtener todas las valoraciones 
    @GetMapping
    public ResponseEntity<List<Valoracion>> obtenerTodasLasValoraciones(){
        List<Valoracion> valoraciones = valoracionService.obtenerTodasLasValoraciones();
        return ResponseEntity.ok(valoraciones);
    }


    

} 

