package com.proyecto.GestionCursos.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idCategoria")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categorias")
public class Categoria {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre_categoria", nullable = false, unique = true)
    private String nombreCategoria;

    //Se hace la relacion con curso

    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    @JsonIgnore
    //@JsonBackReference("curso-categoria")// para evitar bucle en postman
    private List<Curso> cursos = new ArrayList<>();
    

}
