package com.verf_system.verfS.handler;

import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.ErrorResponse;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11


    // @Valid reprovou: junta a mensagem de cada campo"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    // JSON quebrado, texto em campo numérico ou valor de enum que não existe ("motivo": "COMPRAS")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCorpoInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido: confira os valores dos campos (tipo, motivo, origem, nível).");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAcessoNegado(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN, ex.getMessage());
    }


    private ResponseEntity<ErrorResponse> resposta(HttpStatus status, String mensagem) {
        ErrorResponse corpo = ErrorResponse.builder()
                .mensagem(mensagem)
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(corpo);
    }



}
