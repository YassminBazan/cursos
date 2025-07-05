package com.proyecto.GestionCursos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.repository.CategoriaRepository;
import com.proyecto.GestionCursos.repository.CursoRepository;

public class CategoriaServiceTest {

    //Se crean mocks (doble de prueba o simulacion) para los repositorios 
    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CursoRepository cursoRepository;

    //Se crea una instancia real de categoriaService para probar y Mockito le inyecta los mocks de arriba
    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaProgramacion;
    private Categoria categoriaCocina;
    private List<Categoria> listaCategorias;

    //Método que se ejecuta antes de cada prueba para preparar el entorno
    @BeforeEach
    void setUp(){
        //Inicializa los mocks y las inyecciones
        MockitoAnnotations.openMocks(this);

        //Preparacion de un objeto de prueba para varios test 
        categoriaProgramacion = new Categoria();
        categoriaProgramacion.setIdCategoria(1L);
        categoriaProgramacion.setNombreCategoria("Programacion");

        categoriaCocina = new Categoria();
        categoriaCocina.setIdCategoria(2L);
        categoriaCocina.setNombreCategoria("Cocina");

        listaCategorias = List.of(categoriaProgramacion, categoriaCocina);
    
    }



    //PRUEBAS PARA EL MÉTODO CREAR CATEGORIA 

    //@DisplayName nos permite describir el test
    @DisplayName("Test para guardar una categoria de forma exitosa cumpliendo con todos los requerimientos")
    @Test
    void testGuardarCategoriaOk(){
        //ARRANGE (preparar datos)
        String nombreNuevaCategoria = "Gastronomia";
        //Simulamos que no hay ninguna categoria con el mismo nombre
        when(categoriaRepository.findByNombreCategoriaIgnoreCase(nombreNuevaCategoria)).thenReturn(Optional.empty());

        //simulacion de guardar se usa any porque el objeto categoria se crea dentro del servicio
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation ->{
            Categoria cat = invocation.getArgument(0);
            cat.setIdCategoria(2L); //Simula que la bd le asigna un id
            return cat;
        });

        //ACT
        //Se llama al servicio con los parametros que espera
        Categoria resultado = categoriaService.crearCategoria(nombreNuevaCategoria);

        //ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(2L);
        assertThat(resultado.getNombreCategoria()).isEqualTo(nombreNuevaCategoria);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));

    }

    @DisplayName("Test para intentar guardar categoria con un nombre que ya existe")
    @Test
    void testGuardarCategoriaDuplicada(){
        //ARRANGE 
        String nombreDuplicado = "Programacion";
        //Simulamos que ya existe el nombre en el repositorio
        when(categoriaRepository.findByNombreCategoriaIgnoreCase(nombreDuplicado)).thenReturn(Optional.of(categoriaProgramacion));

        //Se verifica que lanza una excepcion
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.crearCategoria(nombreDuplicado);
        });
    
        assertThat(exception.getMessage()).isEqualTo("El nombre de la categoria ya existe");
        //Se verifica que no se intento guardar nada
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @DisplayName("Test para intentar guardar una categoria con un nombre invalido nulo, vacio o en blanco")
    @ParameterizedTest
    @NullAndEmptySource //provee un valor nulo y un string vacio a la prueba
    @ValueSource(strings = {" ", "\t", "\n"}) //provee string con solo espacios en blanco
    void testGuardarCategoriaInvalida(String nombreInvalido){
        //Se hacen las validaciones 
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.crearCategoria(nombreInvalido);
        });
        assertThat(exception.getMessage()).isEqualTo("El nombre de la categoria es obligatorio");
        //Se verifica que no se intento guardar nada
        verify(categoriaRepository, never()).save(any(Categoria.class));

    }

    //PRUEBA PARA OBTENER CATEGORIAS

    @DisplayName("Test para obtener todas las categorias")
    @Test
    void testObtenerTodasLasCategorias(){
        //Arrage: preparacion de datos
        when(categoriaRepository.findAll()).thenReturn(listaCategorias);

        //Act: se ejecuta el metodo
        List<Categoria> resultado = categoriaService.obtenerTodasLasCategorias();

        //Assert: se verifican los resultados
        assertThat(resultado).isNotNull(); 
        assertEquals(2, resultado.size());
        assertEquals("Programacion", resultado.get(0).getNombreCategoria());
        assertEquals("Cocina", resultado.get(1).getNombreCategoria());

        verify(categoriaRepository, times(1)).findAll();

    }

    @DisplayName("Test para obtener categoria por id, deberia devolver la categoria")
    @Test
    void testObtenerCategoriaPorIdOk(){
        //ARRANGE : Preparacion de datos 
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaProgramacion));
        
        //ACT : Se llama el método a probar con los datos preparados
        Categoria resultado = categoriaService.obtenerCategoriaPorId(1L);

        //ASSERT: se verifica el resultado y lo compara con el resultado esperado (con aserciones)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreCategoria()).isEqualTo("Programacion");
    }

    @DisplayName("Test para obtener categoria con un id que no existe")
    @Test
    void testObtenerCategoriaFail(){
        //ARRANGE 
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        //ACCT Y ASSERT
        assertThrows(RuntimeException.class, () -> {
            categoriaService.obtenerCategoriaPorId(99L);
        });
    }

    //TEST PARA OBTENER CATEGORIA POR NOMBRE
    @DisplayName("Test para obtener una categoria por su nombre")
    @Test
    void testObtenerCategoriaPorNombre(){
        //Arrange: preparacion de datos
        when(categoriaRepository.findByNombreCategoriaIgnoreCase("programacion")).thenReturn((Optional.of(categoriaProgramacion)));

        //Act: se llama el metodo a probar
        Optional<Categoria> resultado = categoriaService.buscarCategoriaPorNombre("programacion");

        //Assert: 
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombreCategoria()).isEqualTo("Programacion");
        verify(categoriaRepository, times(1)).findByNombreCategoriaIgnoreCase("programacion");
    }


    @DisplayName("Test para buscar categorias por nombre parcial (contiene texto)")
    @Test
    void testBuscarCategoriasPorNombre_ContieneTexto(){
        // Arrange
        when(categoriaRepository.findByNombreCategoriaContainingIgnoreCase("pro"))
            .thenReturn(List.of(categoriaProgramacion));

        // Act
        List<Categoria> resultado = categoriaService.buscarCategoriasPorNombre("pro");

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreCategoria()).containsIgnoringCase("pro");
        verify(categoriaRepository, times(1)).findByNombreCategoriaContainingIgnoreCase("pro");
    }
    //ACTUALIZAR CATEGORIA
    @DisplayName("Test para actualizar una categoría exitosamente")
    @Test
    void testActualizarCategoriaOk() {
        // Arrange
        Long idCategoria = 1L;
        String nuevoNombre = "Ciencia";

        when(categoriaRepository.findById(idCategoria)).thenReturn(Optional.of(categoriaProgramacion));
        when(categoriaRepository.findByNombreCategoriaIgnoreCase(nuevoNombre)).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Categoria resultado = categoriaService.actualizarCategoria(idCategoria, nuevoNombre);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreCategoria()).isEqualTo(nuevoNombre);
        verify(categoriaRepository).save(categoriaProgramacion);
    }

    @DisplayName("Test para actualizar categoría con nombre vacío")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\n", "\t"})
    void testActualizarCategoriaNombreInvalido(String nombreInvalido) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.actualizarCategoria(1L, nombreInvalido);
        });
        assertThat(exception.getMessage()).isEqualTo("El nuevo nombre no puede estar vacio");

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @DisplayName("Test para actualizar categoría con ID que no existe")
    @Test
    void testActualizarCategoriaIdInexistente() {
        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoriaService.actualizarCategoria(99L, "NuevaCategoria");
        });
        assertThat(exception.getMessage()).isEqualTo("El id ingresado no coincide con ninguna categoria");

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @DisplayName("Test para actualizar categoría con nombre que ya tiene otra categoría")
    @Test
    void testActualizarCategoriaNombreDuplicado() {
        // Arrange
        Long idCategoria = 1L;
        String nombreDuplicado = "Cocina"; // categoríaCocina tiene este nombre y un ID diferente

        when(categoriaRepository.findById(idCategoria)).thenReturn(Optional.of(categoriaProgramacion));
        when(categoriaRepository.findByNombreCategoriaIgnoreCase(nombreDuplicado))
            .thenReturn(Optional.of(categoriaCocina)); // ID diferente al que se está actualizando

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.actualizarCategoria(idCategoria, nombreDuplicado);
        });
        assertThat(exception.getMessage()).isEqualTo("El nombre de la categoria ya existe");

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }


    //PRUEBAS PARA ELIMINAR CATEGORIA
    @DisplayName("Test para eliminar categoría con cursos asociados")
    @Test
    void testEliminarCategoriaConCursosAsociados() {
        Long categoriaId = 1L;

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaProgramacion));

        Curso cursoAsociado = new Curso();
        cursoAsociado.setIdCurso(10L);

        Set<Categoria> categorias = new HashSet<>();
        categorias.add(categoriaProgramacion); // categoriaProgramacion.getIdCategoria() == 1L en setUp()
        cursoAsociado.setCategorias(categorias);

        when(cursoRepository.findByCategorias_IdCategoria(categoriaId)).thenReturn(List.of(cursoAsociado));

        categoriaService.eliminarCategoria(categoriaId);

        // Aquí el removeIf eliminará categoriaProgramacion, removed será true y se llamará a save
        verify(cursoRepository, times(1)).save(cursoAsociado);
        verify(categoriaRepository, times(1)).delete(categoriaProgramacion);

        assertThat(cursoAsociado.getCategorias()).doesNotContain(categoriaProgramacion);
    }


    @DisplayName("Test para eliminar categoría sin cursos asociados")
    @Test
    void testEliminarCategoriaSinCursosAsociados() {
        // Arrange
        Long idCategoria = 1L;

        when(categoriaRepository.findById(idCategoria)).thenReturn(Optional.of(categoriaProgramacion));
        when(cursoRepository.findByCategorias_IdCategoria(idCategoria)).thenReturn(List.of());

        // Act
        categoriaService.eliminarCategoria(idCategoria);

        // Assert
        verify(cursoRepository, never()).save(any(Curso.class));
        verify(categoriaRepository, times(1)).delete(categoriaProgramacion);
    }

    @DisplayName("Test para eliminar categoría inexistente lanza excepción")
    @Test
    void testEliminarCategoriaNoExiste() {
        // Arrange
        Long idCategoria = 99L;
        when(categoriaRepository.findById(idCategoria)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoriaService.eliminarCategoria(idCategoria);
        });

        assertThat(exception.getMessage()).isEqualTo("Categoría no encontrada");

        verify(cursoRepository, never()).save(any());
        verify(categoriaRepository, never()).delete(any());
    }
}
