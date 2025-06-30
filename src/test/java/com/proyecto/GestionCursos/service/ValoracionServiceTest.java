package com.proyecto.GestionCursos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        assertThat(resultado.getPuntuacion()).isEqualTo(4);
        verify(valoracionRepository).save(any(Valoracion.class));
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


}
