package com.proyecto.GestionCursos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "instructores_replicados")
public class InstructorReplicado {

    @Id
    private Long idInstructor;
    
}
