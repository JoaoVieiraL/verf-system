package com.verf_system.verfS.controller;

import com.verf_system.verfS.configuration.TokenService;
import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.dto.request.FuncionarioDto;
import com.verf_system.verfS.dto.request.LoginDto;
import com.verf_system.verfS.dto.response.FuncionarioResponseDto;
import com.verf_system.verfS.dto.response.LoginResponseDto;
import com.verf_system.verfS.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final FuncionarioService funcionarioService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto dados){

        var senhaFuncionario = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        var auth = this.authenticationManager.authenticate(senhaFuncionario);

        var funcionario = (FuncionarioEntity) auth.getPrincipal();

        var token = tokenService.generateToken(funcionario);

        return ResponseEntity.ok(new LoginResponseDto(token, funcionario.getNome(), funcionario.getNivelDeAcesso().name()));
    }


    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponseDto registro(@RequestBody @Valid FuncionarioDto dados){
        return funcionarioService.save(dados);
    }
}
