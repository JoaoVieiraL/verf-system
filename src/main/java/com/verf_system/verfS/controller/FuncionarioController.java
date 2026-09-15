package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.dto.FuncionarioDto;
import com.verf_system.verfS.service.FuncionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Funcionario")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FuncionarioEntity> findAll() {
        return funcionarioService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FuncionarioEntity findById(@PathVariable Long id) {
        return funcionarioService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody FuncionarioDto funcionarioDto) {
        funcionarioService.save(funcionarioDto);
    }

    @DeleteMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        funcionarioService.inativar(id);
    }
}
