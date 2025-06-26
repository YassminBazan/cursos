package com.proyecto.GestionCursos.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data 
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long idCurso;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "nombre_curso", nullable = false)
    private String nombreCurso;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "valor_curso", nullable = false)
    private double valorCurso;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    //Se hace la relacion con categoria
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "cursos_categorias", //Tabla intermedia
        joinColumns = @JoinColumn(name = "id_curso"),
        inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private Set<Categoria> categorias = new HashSet<>();


    //Un curso puede tener varios instructores
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "curso_instructores", joinColumns = @JoinColumn(name = "id_curso"))
    @Column(name = "id_instructor")
    private Set<Long> idsInstructores = new HashSet<>();

}
