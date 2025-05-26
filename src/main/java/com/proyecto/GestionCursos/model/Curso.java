package com.proyecto.GestionCursos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idCurso")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cursos")
public class Curso {

    //Se asigna un ID automaticamente de forma incremental 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long idCurso;
    
    //Se refiere al creador del curso
    @Column(name = "id_usuario")
    private Long idUsuario;

    //Nombre
    @Column(name = "nombre_curso", nullable = false, unique = true)
    private String nombreCurso;

    //columnDefinition --> para cadenas de texto más largas
    @Column(name = "descripcion_curso", columnDefinition = "TEXT")
    private String descripcionCurso;

    @Column(name = "valor_curso")
    private double valorCurso;

    //Estado publicacion del curso, por defecto no esta publicado
    @Column(name = "publicado", nullable = false)
    private boolean publicado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;


    //Relacion con categoria
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name= "curso_categoria",
        joinColumns = @JoinColumn(name = "id_curso"),
        inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    //Inicializa de lista categorias 
    //@JsonManagedReference("curso-categoria") //Se añade para evitar un bucle infinito
    private List<Categoria> categorias = new ArrayList<>();

    //Relacion con recurso
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //Inicializacion de la lista de recursos
    private List<Recurso> recursos = new ArrayList<>();

    //
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "curso_instructores_asignados", 
        joinColumns = @JoinColumn(name = "id_curso")
    )
    @Column(name = "id_instructor_usuario", nullable = false)
    //Inicializacion de lista de instructores
    private List<Long> instructorIds =  new ArrayList<>();

    //Métodos para asignacion de instructores
    public void addInstructor(Long instructorId){
        if (!this.instructorIds.contains(instructorId)) {
            this.instructorIds.add(instructorId);
        }
    }

    public void removeInstructor(Long instructorId){
        //la inicializacion permite que instructorIds nunca sea null aqui
        this.instructorIds.remove(instructorId);
    }

    public void addRecurso(Recurso recurso){
        this.recursos.add(recurso);
        recurso.setCurso(this);
    }

    public void removeRecurso(Recurso recurso){
        this.recursos.remove(recurso);
        recurso.setCurso(null);
    }

    /*/Para asignar la fecha
    @PrePersist
    protected void OnPrePersist(){
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDate.now();
        }

    } */

    @PrePersist
    protected void onCreate(){
        this.fechaCreacion = LocalDate.now();
    }



}
