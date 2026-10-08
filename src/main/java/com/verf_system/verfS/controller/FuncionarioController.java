package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.dto.request.FuncionarioDto;
import com.verf_system.verfS.dto.request.FuncionarioUpdateDto;
import com.verf_system.verfS.dto.response.FuncionarioResponseDto;
import com.verf_system.verfS.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FuncionarioResponseDto> findAll() {
        return funcionarioService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FuncionarioResponseDto findById(@PathVariable Long id) {
        return funcionarioService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody FuncionarioDto funcionario) {
        funcionarioService.save(funcionario);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioUpdateDto funcionario) {
        funcionarioService.atualizar(id, funcionario);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        funcionarioService.excluir(id);
    }
}
