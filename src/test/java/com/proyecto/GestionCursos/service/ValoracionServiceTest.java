package com.proyecto.GestionCursos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.proyecto.GestionCursos.model.Categoria;
import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.model.UsuarioValido;
import com.proyecto.GestionCursos.model.Valoracion;
import com.proyecto.GestionCursos.repository.CursoRepository;
import com.proyecto.GestionCursos.repository.UsuarioValidoRepository;
import com.proyecto.GestionCursos.repository.ValoracionRepository;
public class ValoracionServiceTest {

    //Mocks de prueba de las dependecias 
    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private ValoracionRepository valoracionRepository;

    @Mock
    private UsuarioValidoRepository usuarioValidoRepository;

    //Se crea instancia de ValoracionService 
    @InjectMocks
    private ValoracionService valoracionService;

    //Datos de prueba
    private Curso cursoDePrueba;
    private Valoracion valoracionDePrueba; 
    private UsuarioValido usuarioValidoPrueba;
    private List<Valoracion> listaValoraciones;

    //Método que se ejecuta antes de cada prueba para preparar el entorno
    @BeforeEach
    void setUp(){
        //Se inicializan los mocks 
        MockitoAnnotations.openMocks(this);

        //Preperacion de un objeto para los test

        usuarioValidoPrueba = new UsuarioValido();
        usuarioValidoPrueba.setIdUsuario(10L);

        cursoDePrueba = new Curso();
        cursoDePrueba.setIdCurso(1L);
        cursoDePrueba.setNombreCurso("Curso de Prueba");

        valoracionDePrueba = new Valoracion();
        valoracionDePrueba.setIdValoracion(100L);
        valoracionDePrueba.setCurso(cursoDePrueba);
        valoracionDePrueba.setIdUsuario(10L); 
        valoracionDePrueba.setPuntuacion(4);

        listaValoraciones = List.of(valoracionDePrueba);

    }


    @DisplayName("Test para crear una valoracion correctamente")
    @Test
    void testGuardarValoracionOk(){
        //Arrange 
        Long idUsuario = 10L;
        Long idCurso = 1L;
        //Integer puntuacion = 4; 


        //Simulamos que los datos existen
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(cursoDePrueba));
        when(usuarioValidoRepository.existsById(idUsuario)).thenReturn(true);
    

        //Simulacion metodo save
        when(valoracionRepository.save(any(Valoracion.class))).thenAnswer(invocation -> {
            Valoracion valo = invocation.getArgument(0);
            valo.setIdValoracion(100L);
            return valo;
        });

        //Act 
        Valoracion resultado = valoracionService.crearValoracion(idUsuario, idCurso, 5, "¡Genial!");

        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdValoracion()).isEqualTo(100L);
        assertThat(resultado.getIdUsuario()).isEqualTo(idUsuario);
        assertThat(resultado.getCurso()).isEqualTo(cursoDePrueba);
        assertThat(resultado.getPuntuacion()).isEqualTo(5);
        verify(valoracionRepository).save(any(Valoracion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ID del curso es null")
    void testCrearValoracionConIdCursoNull() {
        Long idUsuario = 1L;
        Integer puntuacion = 5;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearValoracion(idUsuario, null, puntuacion, comentario);
        });

        assertThat(exception.getMessage()).isEqualTo("El ID del curso es obligatorio.");
    }
    @Test
    @DisplayName("Debe lanzar excepción si la puntuación es null")
    void testCrearValoracionPuntuacionNull() {
        Long idUsuario = 10L;
        Long idCurso = 1L;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearValoracion(idUsuario, idCurso, null, comentario);
        });

        assertThat(exception.getMessage()).isEqualTo("La puntuación es obligatoria.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la puntuación es menor a 1")
    void testCrearValoracionPuntuacionMenorA1() {
        Long idUsuario = 10L;
        Long idCurso = 1L;
        Integer puntuacion = 0;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearValoracion(idUsuario, idCurso, puntuacion, comentario);
        });

        assertThat(exception.getMessage()).isEqualTo("La puntuación debe estar entre 1 y 5.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si la puntuación es mayor a 5")
    void testCrearValoracionPuntuacionMayorA5() {
        Long idUsuario = 10L;
        Long idCurso = 1L;
        Integer puntuacion = 6;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearValoracion(idUsuario, idCurso, puntuacion, comentario);
        });

        assertThat(exception.getMessage()).isEqualTo("La puntuación debe estar entre 1 y 5.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el comentario tiene más de 1000 caracteres")
    void testCrearValoracionComentarioMuyLargo() {
        Long idUsuario = 10L;
        Long idCurso = 1L;
        Integer puntuacion = 4;
        String comentarioLargo = "a".repeat(1001);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearValoracion(idUsuario, idCurso, puntuacion, comentarioLargo);
        });

        assertThat(exception.getMessage()).isEqualTo("El comentario no puede exceder los 1000 caracteres.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no está activo")
    void testCrearValoracionUsuarioNoActivo() {
        Long idUsuario = 999L;
        Long idCurso = 1L;
        Integer puntuacion = 4;
        String comentario = "Comentario válido";

        // Simular que el usuario NO existe
        when(usuarioValidoRepository.existsById(idUsuario)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearValoracion(idUsuario, idCurso, puntuacion, comentario);
        });

        assertThat(exception.getMessage()).isEqualTo("Usuario con id " + idUsuario + " no esta activo");
    }

    @DisplayName("Test para obtener todas las valoraciones")
    @Test
    void testObtenerTodasLasValoraciones(){
        //Arrage: preparacion de datos
        when(valoracionRepository.findAll()).thenReturn(listaValoraciones);

        //Act: se ejecuta el metodo
        List<Valoracion> resultado = valoracionService.obtenerTodasLasValoraciones();

        //Assert: se verifican los resultados
        assertThat(resultado).isNotNull(); 
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPuntuacion()).isEqualTo(4);

        verify(valoracionRepository, times(1)).findAll();

    }


    @DisplayName("Test para obtener valoracion por id")
    @Test
    void testObtenerValoracionPorId(){
        //Arrange 
        Long idValoracion = 100L;

        //Simulacion de que si lo encuentra en el repositorio
        when(valoracionRepository.findById(idValoracion)).thenReturn(Optional.of(valoracionDePrueba));
        
        //Act 
        Optional <Valoracion> resultado = valoracionService.obtenerValoracionPorId(idValoracion);

        //Assert 
        //Verificamos que el optional no este vacio
        assertThat(resultado).isPresent();

        //verificamos que los datos coinciden
        assertThat(resultado.get().getIdValoracion()).isEqualTo(idValoracion);
        
        //Verificamos que se llamo al método
        verify(valoracionRepository, times(1)).findById(idValoracion);
    }

    @DisplayName("Test para obtener valoracion por idUsuario")
    @Test
    void testObtenerValoracionPorUsuario(){
        //Arrange 
        Long idUsuario = 10L;

        List<Valoracion> lista = Arrays.asList(valoracionDePrueba);
        //Simulacion de que si lo encuentra en el repositorio
        when(valoracionRepository.findByIdUsuario(idUsuario)).thenReturn(lista);
        
        //Act
        List<Valoracion> resultado = valoracionService.obtenerValoracionPorUsuario(idUsuario);

        //Assert 
        //Verificamos que la lista no este vacia y contenga un elemento
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);

        //Verificamos que se llamo al método
        verify(valoracionRepository, times(1)).findByIdUsuario(idUsuario);
    }


    @DisplayName("Test para obtener valoracion por idCurso")
    @Test
    void testObtenerValoracionPorCurso(){
        //Arrange 
        Long idCurso = 1L;
        List<Valoracion> lista = Arrays.asList(valoracionDePrueba);


        when(cursoRepository.existsById(idCurso)).thenReturn(true);

        //Simulacion de que si lo encuentra en el repositorio
        when(valoracionRepository.findByCurso_IdCurso(idCurso)).thenReturn(lista);
        
        //Act
        List<Valoracion> resultado = valoracionService.obtenerValoracionPorCurso(idCurso);

        //Assert 
        //Verificamos que la lista no este vacia y contenga un elemento
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);

        //Verificamos que se llamo al método
        verify(cursoRepository, times(1)).existsById(idCurso);
        verify(valoracionRepository, times(1)).findByCurso_IdCurso(idCurso);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el curso no existe al obtener sus valoraciones")
    void testObtenerValoracionesPorCursoCursoNoExiste() {
        Long idCursoInexistente = 999L;

        // Simular que el curso no existe
        when(cursoRepository.existsById(idCursoInexistente)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.obtenerValoracionPorCurso(idCursoInexistente);
        });

        assertThat(exception.getMessage()).isEqualTo("No se pueden obtener valoraciones de un curso que no existe.");
    }


    
    @DisplayName("test para actualizar un curso de manera correcta")
    @Test
    void testActualizarValoracion() {
        // Arrange (Preparar)
        Long idValoracion = 100L;
        Long idUsuario = 10L;
        Integer nuevaPuntuacion = 5;
        String nuevoComentario = "Ha mejorado mucho, ¡excelente!";

        // Simulamos que el repositorio encuentra la valoración existente.
        when(valoracionRepository.findById(idValoracion)).thenReturn(Optional.of(valoracionDePrueba));

        when(valoracionRepository.save(any(Valoracion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act 
        Optional<Valoracion> resultadoOpt = valoracionService.actualizarValoracion(idValoracion, nuevaPuntuacion, nuevoComentario, idUsuario);

        // Assert 
        // Verificamos que el Optional contiene un valor
        assertThat(resultadoOpt).isPresent(); 
        
        Valoracion resultado = resultadoOpt.get();

        // Verificamos que los campos se actualizaron
        assertThat(resultado.getPuntuacion()).isEqualTo(nuevaPuntuacion);
        assertThat(resultado.getComentario()).isEqualTo(nuevoComentario);

        // Verificamos que se llamó a los métodos 
        verify(valoracionRepository, times(1)).findById(idValoracion);
        verify(valoracionRepository, times(1)).save(valoracionDePrueba);
    }

    @DisplayName("Debe lanzar excepción si la puntuación es null al actualizar una valoración")
    @Test
    void testActualizarValoracionPuntuacionNull() {
        Long idValoracion = 1L;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.actualizarValoracion(idValoracion, null, comentario, idValoracion);
        });

        assertThat(exception.getMessage()).isEqualTo("La puntuación es obligatoria para actualizar.");
    }
    
    @DisplayName("Debe lanzar excepción si la puntuación es menor a 1")
    @Test
    void testActualizarValoracionPuntuacionMenorA1() {
        Long idValoracion = 1L;
        Long idUsuario = 10L;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.actualizarValoracion(idValoracion, 0, comentario, idUsuario);
        });

        assertThat(exception.getMessage()).isEqualTo("La puntuación debe estar entre 1 y 5.");
    }
        
    @DisplayName("Debe lanzar excepción si la puntuación es mayor a 5")
    @Test
    void testActualizarValoracionPuntuacionMayorA5() {
        Long idValoracion = 1L;
        Long idUsuario = 10L;
        String comentario = "Comentario válido";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.actualizarValoracion(idValoracion, 6, comentario, idUsuario);
        });

        assertThat(exception.getMessage()).isEqualTo("La puntuación debe estar entre 1 y 5.");
    }


    @DisplayName("Debe lanzar excepción si el comentario tiene más de 1000 caracteres")
    @Test
    void testActualizarValoracionComentarioMuyLargo() {
        Long idValoracion = 1L;
        Long idUsuario = 10L;
        Integer puntuacion = 4;
        String comentarioLargo = "a".repeat(1001);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.actualizarValoracion(idValoracion, puntuacion, comentarioLargo, idUsuario);
        });

        assertThat(exception.getMessage()).isEqualTo("El comentario no puede exceder los 1000 caracteres.");
    }

    @DisplayName("Debe retornar Optional.empty si la valoración no existe")
    @Test
    void testActualizarValoracionNoExiste() {
        Long idValoracion = 999L;
        Long idUsuario = 10L;
        Integer puntuacion = 4;
        String comentario = "Comentario válido";

        when(valoracionRepository.findById(idValoracion)).thenReturn(Optional.empty());

        Optional<Valoracion> resultado = valoracionService.actualizarValoracion(idValoracion, puntuacion, comentario, idUsuario);

        assertThat(resultado).isEmpty();
    }

    @DisplayName("Debe lanzar excepción si el usuario no es el dueño de la valoración")
    @Test
    void testActualizarValoracionUsuarioNoAutorizado() {
        Long idValoracion = 1L;
        Long idUsuarioQueNoEsDuenio = 20L;
        Integer puntuacion = 4;
        String comentario = "Comentario válido";

        Valoracion valoracionExistente = new Valoracion();
        valoracionExistente.setIdValoracion(idValoracion);
        valoracionExistente.setIdUsuario(10L); // El dueño real es otro

        when(valoracionRepository.findById(idValoracion)).thenReturn(Optional.of(valoracionExistente));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.actualizarValoracion(idValoracion, puntuacion, comentario, idUsuarioQueNoEsDuenio);
        });

        assertThat(exception.getMessage()).isEqualTo("No tienes permiso para actualizar esta valoración.");
    }

    
    @DisplayName("Debe lanzar excepción si la valoración no existe al eliminar")
    @Test
    void testEliminarValoracionValoracionNoExiste() {
        Long idValoracion = 1L;

        // Simular que no existe la valoración
        when(valoracionRepository.existsById(idValoracion)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.eliminarValoracion(idValoracion);
        });

        assertThat(exception.getMessage()).isEqualTo("La valoracion no existe");
    }

        
    @DisplayName("Debe eliminar la valoración si existe")
    @Test
    void testEliminarValoracionCuandoExiste() {
        Long idValoracion = 1L;

        // Simular que la valoración existe
        when(valoracionRepository.existsById(idValoracion)).thenReturn(true);

        doNothing().when(valoracionRepository).deleteById(idValoracion);

        // Llamada al método a probar
        valoracionService.eliminarValoracion(idValoracion);

        verify(valoracionRepository, times(1)).deleteById(idValoracion);
    }


    




}
