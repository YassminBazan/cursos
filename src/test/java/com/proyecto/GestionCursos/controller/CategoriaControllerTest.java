package com.proyecto.GestionCursos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.service.CategoriaService;

import org.springframework.http.MediaType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import org.w3c.dom.events.EventException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.print.attribute.standard.Media;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    //Permite simular peticiones sin necesidad de levantar un servidor
    @Autowired
    private MockMvc mockMvc;

    //Crea un mock(simulacion) del servicio 
    @MockBean
    private CategoriaService categoriaService;

    //Convierte objetos java a JSON y viceversa
    @Autowired
    private ObjectMapper objectMapper;



    //Test para CREARCATEGORIA
    @Test
    void testCrearCategoria() throws Exception{
        //Simulacion datos de entrada
        Map<String, String> payload = new HashMap<>();
        payload.put("nombreCategoria", "Matematicas");

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setIdCategoria(1L);
        nuevaCategoria.setNombreCategoria("Matematicas");

        //Simulacion del comportamiento del servicio 
        Mockito.when(categoriaService.crearCategoria("Matematicas")).thenReturn(nuevaCategoria);

        //Simulamos y verificamos la peticion 
        mockMvc.perform(post("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCategoria").value(1L))
                .andExpect(jsonPath("$.nombreCategoria").value("Matematicas"));

    }





    // Test para OBTENERCATEGORIAPORID

    @Test
    void testObtenerCategoriaPorId()throws Exception{
        Categoria cat1 = new Categoria();
        cat1.setIdCategoria(2L);
        cat1.setNombreCategoria("Lenguaje");

        //Simualcion comportamiento service
        Mockito.when(categoriaService.obtenerCategoriaPorId(2L))
                .thenReturn(cat1);


        //Simulacion y verificacion de la peticion
        mockMvc.perform(get("/api/v1/categorias/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idCategoria").value(2L))
        .andExpect(jsonPath("$.nombreCategoria").value("Lenguaje"));
    }

    @Test
    void testObtenerCategoriaPorIdNoEncontrado()throws Exception{
        Long idCategoria = 99L;
        Mockito.when(categoriaService.obtenerCategoriaPorId(idCategoria)).thenThrow(new RuntimeException("Categoria no encontrada"));

        mockMvc.perform(get("/api/v1/categorias/" + idCategoria))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Categoria no encontrada"));
    }

    @Test
    void testObtenerCategoriaPorIdErrorServidor()throws Exception{
        Long idCategoria = 5L;

        Mockito.when(categoriaService.obtenerCategoriaPorId(idCategoria)).thenThrow(new Exception("Error inesperado"));

        mockMvc.perform(get("/api/v1/categorias/"+ idCategoria))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al obtener categoria"));
    }


    @Test
    void testObtenerTodasLasCategorias()throws Exception {
        Categoria cat1 = new Categoria();
        cat1.setIdCategoria(1L);
        cat1.setNombreCategoria("Programacion");

        Categoria cat2 = new Categoria();
        cat2.setIdCategoria(2L);
        cat2.setNombreCategoria("Cocina");


        //Simulacion del comportamiento del servicio
        Mockito.when(categoriaService.obtenerTodasLasCategorias())
                .thenReturn(Arrays.asList(cat1, cat2));
        

        
        //Simulamos y verificamos la peticion 
        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCategoria").value("Programacion"))
                .andExpect(jsonPath("$[1].nombreCategoria").value("Cocina"));
    }

    @Test
    void testActualizarCategoria() throws Exception{
        Map<String, String> payload = new HashMap<>();
        payload.put("nuevoNombre", "Ciencias");
        
        Categoria actualizada = new Categoria();
        actualizada.setIdCategoria(1L);
        actualizada.setNombreCategoria("Ciencias");

        //Simulacion del comportamiento del servicio 
        Mockito.when(categoriaService.actualizarCategoria(1L, "Ciencias")).thenReturn(actualizada);

        //simulacion y verificacion de la peticion
        mockMvc.perform(put("/api/v1/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1L))
                .andExpect(jsonPath("$.nombreCategoria").value("Ciencias"));
    }

    @Test
    void testActualizarCategoriaMal() throws Exception{
        Long idCategoria = 1L;

        //nuevoNombres = null
        Map<String, String> payload = new HashMap<>();
        

        //simulacion y verificacion de la peticion
        mockMvc.perform(put("/api/v1/categorias/"+ idCategoria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nuevo nombre es obligatorio"));
        
        //nuevoNombre= ""
        Map<String, String> payloadVacio = Map.of("nuevoNombre", "");

        

        //simulacion y verificacion de la peticion
        mockMvc.perform(put("/api/v1/categorias/"+ idCategoria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payloadVacio)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nuevo nombre es obligatorio"));

        //nuevoNombre = "   "
        Map<String, String> payloadBlanco = Map.of("nuevoNombre", "    ");

        

        //simulacion y verificacion de la peticion
        mockMvc.perform(put("/api/v1/categorias/"+ idCategoria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payloadBlanco)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nuevo nombre es obligatorio"));
    }

    @Test
    void testEliminarCategoria()throws Exception{
        doNothing().when(categoriaService).eliminarCategoria(1L);

        //simulacion y verificacion de la peticion
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());

    }

    @Test
    void testBuscarCategoriaPorNombre() throws Exception{
        Categoria cat1 = new Categoria();
        cat1.setIdCategoria(1L);
        cat1.setNombreCategoria("Biologia");

        Categoria cat2 = new Categoria();
        cat2.setIdCategoria(2L);
        cat2.setNombreCategoria("Biotecnologia");

        //Simulacion del comportamiento del servicio 
        List<Categoria> resultados = Arrays.asList(cat1, cat2);
        Mockito.when(categoriaService.buscarCategoriasPorNombre("Bio")).thenReturn(resultados);

        //simulacion y verificacion de la peticion
        mockMvc.perform(get("/api/v1/categorias/buscar")
                .param("nombre", "Bio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCategoria").value("Biologia"))
                .andExpect(jsonPath("$[1].nombreCategoria").value("Biotecnologia"));

    }





}
