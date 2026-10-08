package com.verf_system.verfS.controller;

import com.verf_system.verfS.dto.request.FornecedorDto;
import com.verf_system.verfS.dto.response.FornecedorResponseDto;
import com.verf_system.verfS.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @GetMapping
    public List<FornecedorResponseDto> findAll() {
        return fornecedorService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FornecedorResponseDto findById(@PathVariable Long id) {
        return fornecedorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorResponseDto save(@Valid @RequestBody FornecedorDto fornecedor) {
        return fornecedorService.save(fornecedor);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorDto fornecedor) {
        fornecedorService.atualizar(id, fornecedor);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        fornecedorService.excluir(id);

    }


}
