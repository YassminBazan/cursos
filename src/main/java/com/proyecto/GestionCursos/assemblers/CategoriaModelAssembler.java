package com.proyecto.GestionCursos.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.proyecto.GestionCursos.controller.CategoriaController;
import com.proyecto.GestionCursos.model.Categoria;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<Categoria, EntityModel<Categoria>> {

    @Override
    public EntityModel<Categoria> toModel(Categoria categoria) {
        return EntityModel.of(categoria,
            linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(categoria.getIdCategoria())).withSelfRel(),
            linkTo(methodOn(CategoriaController.class).obtenerTodasLasCategorias()).withRel("categorias"));
    }
}
