package com.proyecto.GestionCursos.assemblers;

import com.proyecto.GestionCursos.controller.CursoControllerV2;
import com.proyecto.GestionCursos.model.Curso;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CursoModelAssembler implements RepresentationModelAssembler<Curso, EntityModel<Curso>> {

    @Override
    public EntityModel<Curso> toModel(Curso curso) {
        return EntityModel.of(curso,
                linkTo(methodOn(CursoControllerV2.class).obtenerCursoPorId(curso.getIdCurso())).withSelfRel(),
                linkTo(methodOn(CursoControllerV2.class).obtenerTodosLosCursos()).withRel("cursos"),
                linkTo(methodOn(CursoControllerV2.class).asignarInstructor(curso.getIdCurso(), null)).withRel("asignarInstructor"),
                linkTo(methodOn(CursoControllerV2.class).desvincularInstructor(curso.getIdCurso(), null)).withRel("desvincularInstructor")
        );
    }
}
