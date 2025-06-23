package com.proyecto.GestionCursos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.client.UsuarioRegistroClient;
import com.proyecto.GestionCursos.model.UsuarioValido;
import com.proyecto.GestionCursos.repository.UsuarioValidoRepository;

import jakarta.transaction.Transactional;

@Service
public class SincronizadorUsuariosService {
    @Autowired
    private UsuarioRegistroClient usuarioClient;

    @Autowired
    private UsuarioValidoRepository usuarioValidoRepository;

    //Se ejecuta cada 10 min
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void sincronizarUsuarios(){
        System.out.println("Iniciando sincronización de IDs de usuarios válidos...");

        List<Long> idsRemotos = usuarioClient.obtenerUsuariosActivos();

        if (idsRemotos.isEmpty()) {
            System.out.println("No se recibieron IDs de usuarios activos o hubo un error en la comunicación. No se realizarán cambios en la réplica local.");
            return;
        }

        // Borra la tabla actual y la vuelve a llenar con los datos frescos
        usuarioValidoRepository.deleteAllInBatch(); // Más eficiente para borrar todo

        List<UsuarioValido> usuariosParaGuardar = idsRemotos.stream()
                .map(UsuarioValido::new)
                .collect(Collectors.toList());
        
        usuarioValidoRepository.saveAll(usuariosParaGuardar);
        
        System.out.println("Sincronización completada. Total de usuarios válidos replicados: " + usuariosParaGuardar.size());
    }
        
}
