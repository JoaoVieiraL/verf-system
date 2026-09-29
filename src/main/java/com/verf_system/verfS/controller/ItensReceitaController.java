package com.verf_system.verfS.controller;

import com.verf_system.verfS.dto.request.ItensReceitaDto;
import com.verf_system.verfS.dto.response.ItensReceitaResponseDto;
import com.verf_system.verfS.service.ItensReceitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/itens-receita")
@RequiredArgsConstructor
public class ItensReceitaController {

    private final ItensReceitaService itensReceitaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItensReceitaResponseDto> findAll() {
        return itensReceitaService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItensReceitaResponseDto findById(@PathVariable Long id) {
        return itensReceitaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody ItensReceitaDto itensReceitaDto) {
        itensReceitaService.save(itensReceitaDto);
    }
}
