package com.verf_system.verfS.controller;

import com.verf_system.verfS.dto.request.FuncionarioDto;
import com.verf_system.verfS.dto.request.LoginDto;
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
public class AutheticationController {
    private final AuthenticationManager authenticationManager;
    private final FuncionarioService funcionarioService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDto dados){

        var senhaFuncionario = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        var auth = this.authenticationManager.authenticate(senhaFuncionario);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registro(@RequestBody @Valid FuncionarioDto dados){
         funcionarioService.save(dados);
    }
}
