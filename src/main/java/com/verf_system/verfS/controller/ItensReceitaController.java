package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import com.verf_system.verfS.dto.ItensReceitaDto;
import com.verf_system.verfS.service.ItensReceitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ItensReceita")
@RequiredArgsConstructor
public class ItensReceitaController {

    private final ItensReceitaService itensReceitaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItensReceitaEntity> findAll() {
        return itensReceitaService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItensReceitaEntity findById(@PathVariable Long id) {
        return itensReceitaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody ItensReceitaDto itensReceitaDto) {
        itensReceitaService.save(itensReceitaDto);
    }
}
