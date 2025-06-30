package com.proyecto.GestionCursos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.service.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CursoController.class)
public class CursoControllerTest {

    //Permite hacer peticiones sin levantar servidor 
    @Autowired
    private MockMvc mockMvc;

    //Crea un mock del servicio 
    @MockBean
    private CursoService cursoService;

    //Permite convertir objetos java a JSON y viceversa
    @Autowired
    private ObjectMapper objectMapper; 

    private Curso curso1;
    private Curso curso2;

    @BeforeEach
    void setUp(){
        curso1 = new Curso();
        curso1.setIdCurso(1L);
        curso1.setNombreCurso("Java 1");
        curso1.setDescripcion("Curso Java para principiantes");
        curso1.setValorCurso(7000);
        curso1.setIdUsuario(1L);
        curso1.setCategorias(new HashSet<>());

        curso2 = new Curso();
        curso2.setIdCurso(2L);
        curso2.setNombreCurso("Lenguaje");
        curso2.setDescripcion("Curso del señor");
        curso2.setValorCurso(15000);
        curso2.setIdUsuario(1L);
        curso2.setCategorias(new HashSet<>());
        curso1.setFechaCreacion(LocalDate.now());

    }


    @DisplayName("Test para peticion crearCurso")
    @Test
    void testCrearCurso() throws Exception{
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombreCurso", "Java 1");
        payload.put("descripcion", "Curso Java para principiantes");
        payload.put("valorCurso", 7000);
        payload.put("idCreador", 1);
        payload.put("idsCategorias", Arrays.asList(2));



        Curso curso = new Curso();
        curso.setIdCurso(1L);
        curso.setNombreCurso("Java 1");
        curso.setDescripcion("Curso Java para principiantes");
        curso.setValorCurso(7000);
        curso.setIdUsuario(1L);
        curso.setCategorias(new HashSet<>());
        curso.setFechaCreacion(LocalDate.now());

        //Mock del comportamiento del servicio 
        Mockito.when(cursoService.crearCurso(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyDouble(),
                Mockito.anyLong(),
                Mockito.anySet()
        )).thenReturn(curso);

        //simulacion y Verificacion de peticion
        mockMvc.perform(post("/api/v1/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCurso").value(1L))
                .andExpect(jsonPath("$.nombreCurso").value("Java 1"));
    }

    @Test
    void testObtenerTodosLosCursos() throws Exception {

        List<Curso> cursos = Arrays.asList(curso1, curso2);

        //Simulacion del servicio 
        Mockito.when(cursoService.obtenerTodosLosCursos()).thenReturn(cursos);
        
        //Simulacion y verificacion de la peticion
        mockMvc.perform(get("/api/v1/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCurso").value(1L))
                .andExpect(jsonPath("$[0].nombreCurso").value("Java 1"))
                .andExpect(jsonPath("$[1].idCurso").value(2L))
                .andExpect(jsonPath("$[1].nombreCurso").value("Lenguaje"));
    }

    @Test
    void testObtenerCursoPorId()throws Exception{
        //Simulacion service
        Mockito.when(cursoService.obtenerCursoPorId(1L)).thenReturn(Optional.of(curso1));

        //Simulacion y verificacion de peticiones
        mockMvc.perform(get("/api/v1/cursos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idCurso").value(1L))
            .andExpect(jsonPath("$.nombreCurso").value("Java 1"));

    }

    @Test
    void testActualizarCurso()throws Exception{
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombreCurso", "Java v2.0");
        payload.put("descripcion", "Curso java mejorado");
        payload.put("valorCurso", 10000.00);

        Curso actualizado = new Curso();
        actualizado.setIdCurso(1L);
        actualizado.setNombreCurso("Java v2.0");
        actualizado.setDescripcion("Curso java mejorado");
        actualizado.setValorCurso(10000.00);
        actualizado.setIdUsuario(1L);
        actualizado.setFechaCreacion(LocalDate.now());

        //Simulacion del servicio
        Mockito.when(cursoService.actualizarCurso(
            Mockito.eq(1L),
            Mockito.eq("Java v2.0"),
            Mockito.eq("Curso java mejorado"),
            Mockito.eq(10000.00)
            )).thenReturn(Optional.of(actualizado));

        //Simulacion y verificacion de peticion
        mockMvc.perform(put("/api/v1/cursos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCurso").value(1L))
                .andExpect(jsonPath("$.nombreCurso").value("Java v2.0"))
                .andExpect(jsonPath("$.descripcion").value("Curso java mejorado"))
                .andExpect(jsonPath("$.valorCurso").value(10000));

    }

    @Test 
    void testEliminarCurso()throws Exception{
        doNothing().when(cursoService).eliminarCurso(1L);

        //Simulacion y verificacion de la peticion 
        mockMvc.perform(delete("/api/v1/cursos/1"))
                .andExpect(status().isNoContent());
        
    }

    @Test
    void testAsignarInstructor()throws Exception{
        Long idCurso = 1L;
        Long idInstructor = 10L;

        Map<String, Long> payload = new HashMap<>();
        payload.put("idInstructor", idInstructor);

        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        curso.setNombreCurso("Java");
        curso.setDescripcion("Noshe");
        curso.setValorCurso(10000);
        curso.setIdUsuario(idInstructor);
        curso.setFechaCreacion(LocalDate.now());

        Mockito.when(cursoService.asignarInstructor(idCurso, idInstructor)).thenReturn(curso);

        mockMvc.perform(post("/api/v1/cursos/{idCurso}/asignar", idCurso)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCurso").value(1L))
                .andExpect(jsonPath("$.idUsuario").value(10L))
                .andExpect(jsonPath("$.nombreCurso").value("Java"));
    }

    @Test 
    void testDesvincularInstructor()throws Exception{
        Long idCurso = 1L;
        Long idInstructor = 10L;


        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        curso.setNombreCurso("Java");
        curso.setDescripcion("Noshe");
        curso.setValorCurso(10000);
        curso.setIdUsuario(null);
        curso.setFechaCreacion(LocalDate.now());

        Mockito.when(cursoService.desvincularInstructor(idCurso, idInstructor)).thenReturn(curso);

        mockMvc.perform(delete("/api/v1/cursos/{idCurso}/desvincular/{idInstructor}", idCurso, idInstructor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCurso").value(1L))
                .andExpect(jsonPath("$.idUsuario").doesNotExist())
                .andExpect(jsonPath("$.nombreCurso").value("Java"));
    }










}
