package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.dto.FornecedorDto;
import com.verf_system.verfS.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Fornecedor")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @GetMapping
    public List<FornecedorEntity> findAll() {
        return fornecedorService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FornecedorEntity findById(@PathVariable Long id) {
        return fornecedorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody FornecedorDto fornecedor) {
        fornecedorService.save(fornecedor);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        fornecedorService.inativar(id);

    }


}
