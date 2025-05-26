package com.proyecto.GestionCursos.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private Long idUsuario;
    private RolEnum rol;


    

}
