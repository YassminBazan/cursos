package com.proyecto.GestionCursos.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UsuarioRegistroClient {

    private final RestTemplate restTemplate;
    private final String registroBaseUrl;

    public List<Long> obtenerUsuariosActivos(){
        String url = registroBaseUrl + "/ids/activos";

        System.out.println("Llamando a: " + url + " para sincronizar IDs de usuarios."); // Para depuración

         try {
            // una respuesta que se pueda convertir en un array de Long (Long[]).
            ResponseEntity<Long[]> response = restTemplate.getForEntity(url, Long[].class);

            // 3. Procesar la respuesta exitosa.
            if (response.getBody() != null) {
                // Convertir el array Long[] en una lista List<Long> y devolverla.
                return Arrays.asList(response.getBody());
            }

            // Si el cuerpo es nulo por alguna razón, devolvemos una lista vacía para seguridad.
            return Collections.emptyList();

        } catch (RestClientException ex) {
            // 4. Manejar errores de comunicación.
            // Si el MS de Cuentas está caído o no responde, RestTemplate lanzará una RestClientException.
            // Capturamos el error para que nuestro trabajo programado no falle.
            System.err.println("Error al obtener IDs de usuarios activos desde el servicio de registro/cuentas: " + ex.getMessage());
            
            // Devolvemos una lista vacía para indicar al proceso de sincronización
            // que no debe hacer nada y que debe intentarlo de nuevo en el próximo ciclo.
            // ¡Esto es CRUCIAL para no borrar nuestra tabla de réplica si el otro servicio está caído!
            return Collections.emptyList();


            
        }

    }
    
    public List<Long> obtenerIdsDeInstructoresActivos() {
        String url = registroBaseUrl + "/ids/instructores"; // Llama al nuevo endpoint
        try {
            ResponseEntity<Long[]> response = restTemplate.getForEntity(url, Long[].class);
            if (response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return Collections.emptyList();
        } catch (RestClientException ex) {
            System.err.println("Error al obtener IDs de instructores activos desde el servicio de registro: " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}


