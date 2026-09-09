package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.dto.ReceitaDto;
import com.verf_system.verfS.service.ReceitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Receita")
@RequiredArgsConstructor
public class ReceitaController {

    private final ReceitaService receitaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReceitaEntity> findAll() {
        return receitaService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReceitaEntity findById(@PathVariable Long id) {
        return receitaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody ReceitaDto receitaDto) {
        receitaService.save(receitaDto);
    }
}
