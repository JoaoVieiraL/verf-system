package com.verf_system.verfS.controller;

import com.verf_system.verfS.configuration.TokenService;
import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.dto.request.FuncionarioDto;
import com.verf_system.verfS.dto.request.LoginDto;
import com.verf_system.verfS.dto.response.FuncionarioResponseDto;
import com.verf_system.verfS.dto.response.LoginResponseDto;
import com.verf_system.verfS.service.FuncionarioService;
import com.verf_system.verfS.service.LogAcessosService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final FuncionarioService funcionarioService;
    private final TokenService tokenService;
    private final LogAcessosService logAcessosService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto dados, HttpServletRequest request){

        var senhaFuncionario = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        // Registra a tentativa no log de acessos (falha e sucesso) antes de seguir
        Authentication auth;
        try {
            auth = this.authenticationManager.authenticate(senhaFuncionario);
        } catch (AuthenticationException e) {
            registrarAcesso(dados.email(), false, request);
            throw e;
        }
        registrarAcesso(dados.email(), true, request);

        var funcionario = (FuncionarioEntity) auth.getPrincipal();

        var token = tokenService.generateToken(funcionario);

        return ResponseEntity.ok(new LoginResponseDto(token, funcionario.getNome(), funcionario.getNivelDeAcesso().name()));
    }


    // Um erro ao gravar o log não pode impedir o login
    private void registrarAcesso(String email, boolean sucesso, HttpServletRequest request) {
        try {
            logAcessosService.registrarLogin(email, sucesso, ipDaRequisicao(request));
        } catch (RuntimeException e) {
            System.err.println("Não foi possível gravar o log de acesso: " + e.getMessage());
        }
    }

    // Atrás de proxy o IP real vem no cabeçalho X-Forwarded-For (o primeiro da lista)
    private String ipDaRequisicao(HttpServletRequest request) {
        String encaminhado = request.getHeader("X-Forwarded-For");
        if (encaminhado != null && !encaminhado.isBlank()) {
            return encaminhado.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponseDto registro(@RequestBody @Valid FuncionarioDto dados){
        return funcionarioService.save(dados);
    }
}
