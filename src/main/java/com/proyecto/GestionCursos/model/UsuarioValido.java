package com.proyecto.GestionCursos.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios_validos_replica")
public class UsuarioValido {

    @Id
    private Long idUsuario;


}
