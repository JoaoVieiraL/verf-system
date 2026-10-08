package com.verf_system.verfS.controller;

import com.verf_system.verfS.dto.request.EstoqueRequestDto;
import com.verf_system.verfS.dto.request.EstoqueUpdateDto;
import com.verf_system.verfS.dto.response.EstoqueResponseDto;
import com.verf_system.verfS.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EstoqueResponseDto> findAll() {
        return estoqueService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EstoqueResponseDto findById(@PathVariable Long id) {
        return estoqueService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid  @RequestBody EstoqueRequestDto estoqueDto) {
        estoqueService.save(estoqueDto);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@PathVariable Long id, @Valid @RequestBody EstoqueUpdateDto estoqueDto) {
        estoqueService.atualizar(id, estoqueDto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        estoqueService.excluir(id);
    }
}
