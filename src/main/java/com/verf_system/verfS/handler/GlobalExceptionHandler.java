package com.verf_system.verfS.handler;

import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.ErrorResponse;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNaoEncontradoException(NaoEncontradoException ex){
        ErrorResponse  errorResponse = ErrorResponse.builder()
                .mensagem(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DadoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDadoDuplicadoException(DadoDuplicadoException ex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .mensagem(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraDeNegocioException(RegraDeNegocioException ex){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .mensagem(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
