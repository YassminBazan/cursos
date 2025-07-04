package com.proyecto.GestionCursos.assemblers;

import com.proyecto.GestionCursos.controller.ValoracionControllerV2;
import com.proyecto.GestionCursos.model.Valoracion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ValoracionModelAssembler implements RepresentationModelAssembler<Valoracion, EntityModel<Valoracion>> {

    @Override
    public EntityModel<Valoracion> toModel(Valoracion valoracion) {
        return EntityModel.of(valoracion,
                linkTo(methodOn(ValoracionControllerV2.class).obtenerValoracionPorId(valoracion.getIdValoracion())).withSelfRel(),
                linkTo(methodOn(ValoracionControllerV2.class).obtenerTodasLasValoraciones()).withRel("valoraciones"));
    }
}
