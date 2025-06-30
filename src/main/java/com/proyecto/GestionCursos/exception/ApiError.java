package com.proyecto.GestionCursos.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private String mensaje;
    private int codigo;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiError(String mensaje, int codigo) {
    this.mensaje = mensaje;
    this.codigo = codigo;
    this.timestamp = LocalDateTime.now();


    }


}
