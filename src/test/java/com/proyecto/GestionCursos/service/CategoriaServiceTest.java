package com.proyecto.GestionCursos.service;

import static org.assertj.core.api.Assertions.assertThat;
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

    //Método que se ejecuta antes de cada prueba para preparar el entorno
    @BeforeEach
    void setUp(){
        //Inicializa los mocks y las inyecciones
        MockitoAnnotations.openMocks(this);

        //Preparacion de un objeto de prueba para varios test 
        categoriaProgramacion = new Categoria();
        categoriaProgramacion.setIdCategoria(1L);
        categoriaProgramacion.setNombreCategoria("Programacion");
    
    }

    //PRUEBAS PARA EL MÉTODO CREAR CATEGORIA 

    //@DisplayName nos permite describir el test
    @DisplayName("Test para guardar una categoria de forma exitosa")
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

    //PRUEBAS PARA EL MÉTODO OBTENER CATEGORIA POR ID

    @DisplayName("Test para obtener categoria por id, deberia devolver la categoria")
    @Test
    void testObtenerCategoriaOk(){
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
            categoriaService.obtenerCategoriaPorId(99l);
        });
    }

    //PRUEBAS PARA ELIMINAR CATEGORIA
    @DisplayName("Test para cuando se elimine una categoria primero debe desvincular los cursos asociados")
    @Test
    void testEliminarCategoria(){
        //ARRANGE 
        Long idAEliminar = 1L;
        Curso cursoAsociado = new Curso(); //Se crea un curso de prueba
        //Se utiliza un HashSet mutable para verificar la eliminacion
        cursoAsociado.setCategorias(new HashSet<>(Arrays.asList(categoriaProgramacion)));

        when(categoriaRepository.findById(idAEliminar)).thenReturn(Optional.of(categoriaProgramacion));
        when(cursoRepository.findByCategorias_IdCategoria(idAEliminar)).thenReturn(List.of(cursoAsociado));

        //ACT 
        categoriaService.eliminarCategoria(idAEliminar);

        //ASSERT: Se verifica que el curso se guardo 
        verify(cursoRepository, times(1)).save(cursoAsociado);
        //Verificamos que la categoria fue eliminada
        verify(categoriaRepository, times(1)).delete(categoriaProgramacion);
        //Verificamos que la coleecion de categorias esta vacia
        assertThat(cursoAsociado.getCategorias()).isEmpty();
    }

    @DisplayName("Test para eliminar una categoria que no existe")
    @Test 
    void testEliminarCategoriaFail(){
        //Arrange 
        Long idCategoriaFake = 100L;

        //Simulamos que la categoria no existe
        when(categoriaRepository.existsById(idCategoriaFake)).thenReturn(false);

        //Verificamos la excepcion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoriaService.eliminarCategoria(idCategoriaFake);
        });

        //Verificacion del mensaje 
        assertThat(exception.getMessage()).isEqualTo("Categoría no encontrada");
    }



}
