package com.verf_system.verfS.controller;

import com.verf_system.verfS.dto.request.ProducoesDto;
import com.verf_system.verfS.dto.response.ProducoesResponseDto;
import com.verf_system.verfS.service.ProducoesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Producoes")
@RequiredArgsConstructor
public class ProducoesController {

    private final ProducoesService producoesService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProducoesResponseDto> findAll() {
        return producoesService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProducoesResponseDto findById(@PathVariable Long id) {
        return producoesService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody ProducoesDto producoesDto) {
        producoesService.save(producoesDto);
    }
}
