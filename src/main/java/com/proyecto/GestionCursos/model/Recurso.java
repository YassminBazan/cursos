package com.proyecto.GestionCursos.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idRecurso")

@Data
@Entity
@Table(name = "recursos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    private Long idRecurso;

    //Id del instructor que crea el recurso
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_recurso", nullable = false)
    private String nombreRecurso;

    @Column(name = "contenido_recurso", columnDefinition = "TEXT")
    private String contenidoRecurso;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRecursoEnum estado;

    //Relacion con curso
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_curso", nullable = false)
    @JsonIgnore
    private Curso curso;

    //Se asigna la fecha y un estado por defecto
    public void inicializarCreacion(){
        this.fechaCreacion = LocalDate.now();
        this.estado = EstadoRecursoEnum.PENDIENTE_DE_REVISION;
    }

    //Con @PrePersist no es necesario llamar al método manualmenre en el service
    @PrePersist
    protected void onCreate(){
        this.fechaCreacion = LocalDate.now();
        this.estado = EstadoRecursoEnum.PENDIENTE_DE_REVISION;
    }


}
