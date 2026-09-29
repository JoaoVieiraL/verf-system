package com.verf_system.verfS.controller;

import com.verf_system.verfS.dto.request.MovimentacaoEstoqueDto;
import com.verf_system.verfS.dto.response.MovimentacaoEstoqueResponseDto;
import com.verf_system.verfS.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/movimentacao-estoque")
@RequiredArgsConstructor
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MovimentacaoEstoqueResponseDto> findAll() {
        return movimentacaoEstoqueService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MovimentacaoEstoqueResponseDto findById(@PathVariable Long id) {
        return movimentacaoEstoqueService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody MovimentacaoEstoqueDto movimentacaoEstoqueDto) {
        movimentacaoEstoqueService.save(movimentacaoEstoqueDto);
    }
}
