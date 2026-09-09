package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.FinanceiroEntity;
import com.verf_system.verfS.dto.FinanceiroDto;
import com.verf_system.verfS.service.FinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Financeiro")
@RequiredArgsConstructor
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FinanceiroEntity> findAll() {
        return financeiroService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FinanceiroEntity findById(@PathVariable Long id) {
        return financeiroService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody FinanceiroDto financeiroDto) {
        financeiroService.save(financeiroDto);
    }
}
