package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.dto.EstoqueDto;
import com.verf_system.verfS.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EstoqueEntity> findAll() {
        return estoqueService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EstoqueEntity findById(@PathVariable Long id) {
        return estoqueService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody EstoqueDto estoqueDto) {
        estoqueService.save(estoqueDto);
    }
}
