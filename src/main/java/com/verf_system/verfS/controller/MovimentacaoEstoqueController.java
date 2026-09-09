package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.MovimentacaoEstoqueEntity;
import com.verf_system.verfS.dto.MovimentacaoEstoqueDto;
import com.verf_system.verfS.service.MovimentacaoEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/MovimentacaoEstoque")
@RequiredArgsConstructor
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MovimentacaoEstoqueEntity> findAll() {
        return movimentacaoEstoqueService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MovimentacaoEstoqueEntity findById(@PathVariable Long id) {
        return movimentacaoEstoqueService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody MovimentacaoEstoqueDto movimentacaoEstoqueDto) {
        movimentacaoEstoqueService.save(movimentacaoEstoqueDto);
    }
}
