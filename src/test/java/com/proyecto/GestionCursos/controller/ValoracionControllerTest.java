package com.proyecto.GestionCursos.controller;

import com.proyecto.GestionCursos.model.Valoracion;
import com.proyecto.GestionCursos.model.Curso; // Importamos la clase Curso
import com.proyecto.GestionCursos.service.ValoracionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValoracionControllerTest {

    @Mock
    private ValoracionService valoracionService;

    @InjectMocks
    private ValoracionController valoracionController;

    private Valoracion valoracion1;
    private Valoracion valoracion2;
    private Curso cursoEjemplo; // Objeto Curso para asociar a las valoraciones

    @BeforeEach
    void setUp() {
        // Inicializamos un objeto Curso que será utilizado por las valoraciones
        cursoEjemplo = new Curso();
        cursoEjemplo.setIdCurso(1L);
        cursoEjemplo.setNombreCurso("Programación Java Avanzada");
        cursoEjemplo.setDescripcion("Curso completo de Java para expertos.");
        cursoEjemplo.setValorCurso(99.99);
        cursoEjemplo.setFechaCreacion(LocalDate.now());
        // No necesitamos inicializar categorías o instructores para estas pruebas específicas del controlador de valoración

        valoracion1 = new Valoracion();
        valoracion1.setIdValoracion(1L);
        valoracion1.setIdUsuario(101L);
        valoracion1.setPuntuacion(5);
        valoracion1.setComentario("Excelente curso, muy bien explicado.");
        valoracion1.setFechaCreacion(LocalDate.now());
        valoracion1.setCurso(cursoEjemplo); // Asociamos el curso

        valoracion2 = new Valoracion();
        valoracion2.setIdValoracion(2L);
        valoracion2.setIdUsuario(102L);
        valoracion2.setPuntuacion(4);
        valoracion2.setComentario("Contenido relevante, aunque un poco rápido.");
        valoracion2.setFechaCreacion(LocalDate.now());
        valoracion2.setCurso(cursoEjemplo); // Asociamos el curso
    }

    @Test
    void crearValoracion_shouldReturnCreatedValoracion() {
        // Given
        Map<String, Object> payload = Map.of(
                "idUsuario", 101L,
                "idCurso", 1L,
                "puntuacion", 5,
                "comentario", "Excelente curso, muy bien explicado."
        );
        when(valoracionService.crearValoracion(anyLong(), anyLong(), anyInt(), anyString()))
                .thenReturn(valoracion1);

        // When
        ResponseEntity<?> response = valoracionController.crearValoracion(payload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(valoracion1, response.getBody());
    }

    @Test
    void actualizarValoracion_shouldReturnUpdatedValoracion() {
        // Given
        Long idValoracion = 1L;
        Map<String, Object> payload = Map.of(
                "idUsuario", 101L, // idUsuarioQueActualiza
                "puntuacion", 4,
                "comentario", "Curso actualizado, ahora está mejor."
        );
        Valoracion updatedValoracion = new Valoracion();
        updatedValoracion.setIdValoracion(1L);
        updatedValoracion.setIdUsuario(101L);
        updatedValoracion.setPuntuacion(4);
        updatedValoracion.setComentario("Curso actualizado, ahora está mejor.");
        updatedValoracion.setFechaCreacion(LocalDate.now());
        updatedValoracion.setCurso(cursoEjemplo);

        when(valoracionService.actualizarValoracion(eq(idValoracion), anyInt(), anyString(), anyLong()))
                .thenReturn(Optional.of(updatedValoracion));

        // When
        ResponseEntity<?> response = valoracionController.actualizarValoracion(idValoracion, payload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedValoracion, response.getBody());
    }

    @Test
    void actualizarValoracion_shouldReturnNotFoundWhenValoracionDoesNotExist() {
        // Given
        Long idValoracion = 99L; // Un ID que no existe
        Map<String, Object> payload = Map.of(
                "idUsuario", 101L,
                "puntuacion", 4,
                "comentario", "Intento de actualización para valoración inexistente."
        );
        when(valoracionService.actualizarValoracion(eq(idValoracion), anyInt(), anyString(), anyLong()))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = valoracionController.actualizarValoracion(idValoracion, payload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Map.of("error", "Valoración no encontrada."), response.getBody());
    }

    @Test
    void obtenerValoracionPorId_shouldReturnValoracion() {
        // Given
        Long idValoracion = 1L;
        when(valoracionService.obtenerValoracionPorId(idValoracion))
                .thenReturn(Optional.of(valoracion1));

        // When
        ResponseEntity<Valoracion> response = valoracionController.obtenerValoracionPorId(idValoracion);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(valoracion1, response.getBody());
    }

    @Test
    void obtenerValoracionPorId_shouldReturnNotFound() {
        // Given
        Long idValoracion = 99L; // ID que no existe
        when(valoracionService.obtenerValoracionPorId(idValoracion))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<Valoracion> response = valoracionController.obtenerValoracionPorId(idValoracion);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void obtenerValoracionesPorUsuario_shouldReturnListOfValoraciones() {
        // Given
        Long idUsuario = 101L;
        List<Valoracion> valoracionesUsuario = Arrays.asList(valoracion1);
        when(valoracionService.obtenerValoracionPorUsuario(idUsuario))
                .thenReturn(valoracionesUsuario);

        // When
        ResponseEntity<List<Valoracion>> response = valoracionController.obtenerValoracionesPorUsuario(idUsuario);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(valoracionesUsuario, response.getBody());
    }

    @Test
    void obtenerValoracionesPorCurso_shouldReturnListOfValoraciones() {
        // Given
        Long idCurso = 1L;
        List<Valoracion> valoracionesCurso = Arrays.asList(valoracion1, valoracion2);
        when(valoracionService.obtenerValoracionPorCurso(idCurso))
                .thenReturn(valoracionesCurso);

        // When
        ResponseEntity<List<Valoracion>> response = valoracionController.obtenerValoracionesPorCurso(idCurso);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(valoracionesCurso, response.getBody());
    }

    @Test
    void obtenerTodasLasValoraciones_shouldReturnListOfAllValoraciones() {
        // Given
        List<Valoracion> todasLasValoraciones = Arrays.asList(valoracion1, valoracion2);
        when(valoracionService.obtenerTodasLasValoraciones())
                .thenReturn(todasLasValoraciones);

        // When
        ResponseEntity<List<Valoracion>> response = valoracionController.obtenerTodasLasValoraciones();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todasLasValoraciones, response.getBody());
    }
}