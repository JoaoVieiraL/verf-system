package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.ProducoesEntity;
import com.verf_system.verfS.dto.ProducoesDto;
import com.verf_system.verfS.service.ProducoesService;
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
    public List<ProducoesEntity> findAll() {
        return producoesService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProducoesEntity findById(@PathVariable Long id) {
        return producoesService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody ProducoesDto producoesDto) {
        producoesService.save(producoesDto);
    }
}
