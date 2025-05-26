package com.proyecto.GestionCursos.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.model.RolEnum;

import com.proyecto.GestionCursos.model.Usuario;


@Service
public class UsuarioRolService {

    private final Map<Long, Usuario> usuariosRegistrados = new HashMap<>();

    //Usuarios de prueba
    public UsuarioRolService(){

    usuariosRegistrados.put(100L, new Usuario(100L, RolEnum.GERENTE_DE_CURSOS));
    usuariosRegistrados.put(101L, new Usuario(101L, RolEnum.INSTRUCTOR));
    }

    public Optional<Usuario> findUserById(Long idUsuario){
        return Optional.ofNullable(usuariosRegistrados.get(idUsuario));
    }

    public boolean tieneRol(Long idUsuario, RolEnum rolEsperado){
        return findUserById(idUsuario)
                .map(usuario -> usuario.getRol() == rolEsperado)
                .orElse(false);
    }
}
