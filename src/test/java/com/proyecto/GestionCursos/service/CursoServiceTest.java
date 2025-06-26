package com.proyecto.GestionCursos.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.repository.CategoriaRepository;
import com.proyecto.GestionCursos.repository.CursoRepository;
import com.proyecto.GestionCursos.repository.InstructorReplicadoRepository;
import com.proyecto.GestionCursos.repository.UsuarioValidoRepository;

public class CursoServiceTest {

    //Se crean mocks (doble de prueba o simulacion) de las dependendencias del servicio para los repositorios 
    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioValidoRepository usuarioValidoRepository;

    @Mock
    private InstructorReplicadoRepository instructorReplicadoRepository;

    //Se crea una instancia real de cursoService para probar y mockito le inyecta los mocks de arriba 
    @InjectMocks
    private CursoService cursoService;

    private Curso cursoPrueba;
    
    //Método que se ejecuta antes de cada prueba para preparar el entorno
    @BeforeEach
    void setUp(){
        //Se inicializan los mocks y las inyecciones 
        MockitoAnnotations.openMocks(this);


        //Preparacion de un objeto de prueba para varios test
        cursoPrueba = new Curso();
        cursoPrueba.setIdCurso(1L);
        cursoPrueba.setIdUsuario(11L);
        cursoPrueba.setNombreCurso("Python");
        cursoPrueba.setDescripcion("Curso Python para principiantes");
        cursoPrueba.setValorCurso(5000);

    }


    @DisplayName("Test para probar el método crearCurso exitosamente")
    @Test
    void testGuardarCursoOk(){
        //ARRANGE: Asignacion de valores a las variables
        Long idCreador = 1L;
        Set<Long> idsCategorias = Set.of(10L);
        Categoria categoriaExistente = new Categoria(10L, "Programacion", Collections.emptyList());

        //Simulacion de validaciones
        //Usuario creador existe
        when(usuarioValidoRepository.existsById(idCreador)).thenReturn(true);
        //Categoria con id 10L existe
        when(categoriaRepository.findById(10L)).thenReturn(Optional.of(categoriaExistente));

        //Simulacion para guardar
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> {
            //Index hace referencia al primero elemento 
            Curso curso = invocation.getArgument(0);
            curso.setIdCurso(100L); //Simula la asignacion de un id
            return curso;
        });

        //ACT 
        Curso resultado = cursoService.crearCurso("Curso python 1", "Curso nivel básico para principiantes", 10000, idCreador, idsCategorias);

        //ASSERT 
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCurso()).isEqualTo(100L);
        assertThat(resultado.getIdUsuario()).isEqualTo(idCreador);
        assertThat(resultado.getCategorias()).contains(categoriaExistente);
        verify(cursoRepository).save(any(Curso.class));

    }

    @DisplayName("Test para probar el método crearCurso de manera fallida, porque el creador no existe ")
    @Test
    void testGuardarCursoFail(){
        //ARRANGE: Preparacion de datos 
        Long idCreadorInvalido = 99L;

        //Simulamos que el usuario creador no existe 
        when(usuarioValidoRepository.existsById(idCreadorInvalido)).thenReturn(false);

        //ACT Y ASSERT 
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.crearCurso("Curso fail", "", 5000, idCreadorInvalido, Set.of(1L));
        });

        //Verificamos que no se intento guardar el curso
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @DisplayName("Test para probar el método asignarInstructor correctamente para agregarlo a la lista de instructores del curso")
    @Test
    void testAsignarInstructorOk(){
        //ARRANGE: Preparacion de los datos, asignacion de valores
        Long idCurso = 1L;
        Long idInstructor = 20L;
        Curso cursoExistente = new Curso();
        cursoExistente.setIdsInstructores(new HashSet<>()); //Inicializa la colección 

        //Simulamos que el instructor si existe en la tabla replicada
        when(instructorReplicadoRepository.existsById(idInstructor)).thenReturn(true);
        //Lo mismo para el curso
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(cursoExistente));
        //Simulacion de guardar
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente);

        //ACT
        Curso resultado = cursoService.asignarInstructor(idCurso, idInstructor);

        //ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdsInstructores()).contains(idInstructor);
        verify(cursoRepository).save(cursoExistente);

    }

    @DisplayName("Test para probar el método asignarInstructor de manera incorrecta, por no existencia de de instructor")
    @Test
    void testAsignarInstructorFail(){
        Long idCurso = 1L;
        Long idInstructorInvalido = 99L;

        //Simulamos que el instructor no existe
        when(instructorReplicadoRepository.existsById(idInstructorInvalido)).thenReturn(false);

        //ACT: Para llamr al método o funcion a probar y ASSERT: Verifica que el resultado obtenido sea el esperado
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.asignarInstructor(idCurso, idInstructorInvalido);

        });

    }
    @DisplayName("Test para desvincular a un instructor de manera exitosa")
    @Test
    void testDesvincularInstructorOk(){
        Long idCurso = 1L;
        Long idInstructor = 10L;

        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        Set<Long> instructores = new HashSet<>();
        instructores.add(idInstructor);
        curso.setIdsInstructores(instructores);



        //Simulacion de que el curso existe
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation ->invocation.getArgument(0));

        //ACT
        Curso resultado = cursoService.desvincularInstructor(idCurso, idInstructor);

        //ASSERT
        assertThat(resultado).isNotNull();

        //Se verifica que la lista de instructores no contenga el id del instructor eliminado
        assertThat(resultado.getIdsInstructores()).doesNotContain(idInstructor);
        
    }


    @DisplayName("Test para desvincular a un instructor de manera fallida ")
    @Test
    void testDesvincularIntructorFail(){
        Long idCursoInvalido = 1L;
        Long idInstructor = 10L; 
        
        //Simulacion de que el curso no existe 
        when(cursoRepository.findById(idCursoInvalido)).thenReturn(Optional.empty());

        //ACT
        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.desvincularInstructor(idCursoInvalido, idInstructor);
        });
    }

    @DisplayName("Test para actualizar curso de manera correcta")
    @Test
    void testActualizarCursoOk(){
        //Preparacion de datos existentes
        Long idCursoExistente = 1L;
        String nuevoNombre = "Curso Python 2.0";
        String nuevaDescripcion = "Curso python version actualizada";
        double nuevoValor = 6000;

        //Simulacion de que el curso existe
        when(cursoRepository.findById(idCursoExistente)).thenReturn(Optional.of(cursoPrueba));

        //Simulacion del metodo 
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act 
        Optional<Curso> resultadoOpt = cursoService.actualizarCurso(idCursoExistente,nuevoNombre, nuevaDescripcion, nuevoValor);

        //Verificacion de que el optional no esta vacio
        assertThat(resultadoOpt).isPresent();

        Curso resultado = resultadoOpt.get();

        //Verificamos que se actualizaron correctamente los campos
        assertThat(resultado.getNombreCurso()).isEqualTo(nuevoNombre);
        assertThat(resultado.getDescripcion()).isEqualTo(nuevaDescripcion);
        assertThat(resultado.getValorCurso()).isEqualTo(nuevoValor);
    }

    @DisplayName("Test para actualizar un curso de manera incorrecta")
    @Test
    void testactualizarCursoFail(){

        //Preparacion de datos existentes
        Long idCurso = 100L;

        //Simulacion de que el curso existe
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.empty());

        //Act
        Optional<Curso> resultadoOpt = cursoService.actualizarCurso(idCurso, "BD", "Quien sabe", 10000);

        //Assert
        //Verificacion del optional 
        assertThat(resultadoOpt).isEmpty();

        //verificacion de que no se uso el metodo
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @DisplayName("Test para eliminar un curso de manera correcta")
    @Test
    void testEliminarCursoOk(){
        //Arrange
        Long idCursoExistente = 1L;

        //Simulacion de que el curso si existe
        when(cursoRepository.existsById(idCursoExistente)).thenReturn(true);

        //Act 
        cursoService.eliminarCurso(idCursoExistente);

        //Verificamos que el metodo delete se llamo correctamente 
        verify(cursoRepository, times(1)).deleteById(idCursoExistente);
    }

    @DisplayName("Test para eliminar un curso que no existe")
    @Test
    void testEliminarCursoFail(){
        //Arrange 
        Long idCursofake = 100L;

        //Simulamos que el curso no existe
        when(cursoRepository.existsById(idCursofake)).thenReturn(false);

        //Verificamos que se lanza una excepcion
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursoService.eliminarCurso(idCursofake);
        });
        //Verificacion del mensaje
        assertThat(exception.getMessage()).isEqualTo("El curso ingresado no fue encontrado");
    }

    

}
