package com.proyecto.GestionCursos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.model.Curso;
import com.proyecto.GestionCursos.model.Valoracion;
import com.proyecto.GestionCursos.repository.CursoRepository;
import com.proyecto.GestionCursos.repository.UsuarioValidoRepository;
import com.proyecto.GestionCursos.repository.ValoracionRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ValoracionService {

    private final CursoRepository cursoRepository;
    private final ValoracionRepository valoracionRepository;
    private final UsuarioValidoRepository usuarioValidoRepository;


    //Para crear una valoracion
    @Transactional
    public Valoracion crearValoracion(Long idUsuario, Long idCurso, Integer puntuacion, String comentario){

        if (idCurso == null) {
            throw new IllegalArgumentException("El ID del curso es obligatorio.");
        }
        if (puntuacion == null) {
            throw new IllegalArgumentException("La puntuación es obligatoria.");
        }

        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }
        if (comentario != null && comentario.length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede exceder los 1000 caracteres.");
        }

        //Validacion de usuario
        if(!usuarioValidoRepository.existsById(idUsuario)){
            throw new IllegalArgumentException("Usuario con id " + idUsuario + " no esta activo");
        }
        //Validacion de curso
        Curso cursoAValorar = cursoRepository.findById(idCurso)
            .orElseThrow(() -> new IllegalArgumentException("El curso con ID " + idCurso + " no existe."));

        Valoracion nuevaValoracion = new Valoracion();
        nuevaValoracion.setCurso(cursoAValorar);
        nuevaValoracion.setIdUsuario(idUsuario);
        nuevaValoracion.setPuntuacion(puntuacion);
        nuevaValoracion.setComentario(comentario);
        nuevaValoracion.setFechaCreacion(LocalDate.now());

        return valoracionRepository.save(nuevaValoracion);
    }

    //Obtener todas las valoraciones
    public List<Valoracion> obtenerTodasLasValoraciones(){
        return valoracionRepository.findAll();
    }
    public Optional<Valoracion> obtenerValoracionPorId(Long idValoracion){
        return valoracionRepository.findById(idValoracion);
    }

    public List<Valoracion> obtenerValoracionPorUsuario(Long idUsuario){
        return valoracionRepository.findByIdUsuario(idUsuario);
    }

    public List<Valoracion> obtenerValoracionPorCurso(Long idCurso){
        if (!cursoRepository.existsById(idCurso)) {
            throw new IllegalArgumentException("No se pueden obtener valoraciones de un curso que no existe.");
        }
        return valoracionRepository.findByCurso_IdCurso(idCurso);
    }

    @Transactional
    public Optional<Valoracion> actualizarValoracion(Long idValoracion, Integer puntuacion, String comentario, Long idUsuario){

        if (puntuacion == null) {
            throw new IllegalArgumentException("La puntuación es obligatoria para actualizar.");
        }
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }
        if (comentario != null && comentario.length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede exceder los 1000 caracteres.");
        }

        Optional<Valoracion> valoracionOpt = valoracionRepository.findById(idValoracion);

        
        if (valoracionOpt.isEmpty()) {
            return Optional.empty(); 
        }

        Valoracion valoracionExistente = valoracionOpt.get();

        if (!valoracionExistente.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("No tienes permiso para actualizar esta valoración.");
        }


        valoracionExistente.setPuntuacion(puntuacion);
        valoracionExistente.setComentario(comentario);

        Valoracion valoracionActualizada = valoracionRepository.save(valoracionExistente);
        return Optional.of(valoracionActualizada);
    }

    @Transactional
    public void eliminarValoracion(Long idValoracion){
        if (!valoracionRepository.existsById(idValoracion)) {
            throw new IllegalArgumentException("La valoracion no existe");
        }
        valoracionRepository.deleteById(idValoracion);
    }

}
