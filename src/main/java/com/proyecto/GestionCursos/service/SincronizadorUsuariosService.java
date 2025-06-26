package com.proyecto.GestionCursos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proyecto.GestionCursos.client.UsuarioRegistroClient;
import com.proyecto.GestionCursos.model.InstructorReplicado;
import com.proyecto.GestionCursos.model.UsuarioValido;
import com.proyecto.GestionCursos.repository.InstructorReplicadoRepository;
import com.proyecto.GestionCursos.repository.UsuarioValidoRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SincronizadorUsuariosService {

    private final UsuarioRegistroClient usuarioClient;
    private final UsuarioValidoRepository usuarioValidoRepository;
    private final InstructorReplicadoRepository instructorReplicadoRepository;

    //Se ejecuta cada 10 min
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void sincronizarDatos(){
        System.out.println("Iniciando sincronización de IDs de usuarios válidos...");

        //Llama a los metodos privados
        sincronizarUsuarios();
        sincronizarInstructores();
        
    }
    private void sincronizarUsuarios(){

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


    private void sincronizarInstructores() {
        System.out.println("Sincronizando IDs de instructores válidos...");
        List<Long> idsInstructoresRemotos = usuarioClient.obtenerIdsDeInstructoresActivos(); // Llama al nuevo método del cliente

        if (idsInstructoresRemotos.isEmpty()) {
            System.out.println("No se recibieron IDs de instructores. La tabla de réplica no se modificó.");
            return;
        }

        instructorReplicadoRepository.deleteAllInBatch();

        List<InstructorReplicado> instructoresParaGuardar = idsInstructoresRemotos.stream()
                .map(InstructorReplicado::new) // Asume que tienes una entidad InstructorReplicado similar a UsuarioValido
                .collect(Collectors.toList());
        
        instructorReplicadoRepository.saveAll(instructoresParaGuardar);
        System.out.println("Sincronización completada. Total de instructores válidos replicados: " + instructoresParaGuardar.size());
    }
        
}
