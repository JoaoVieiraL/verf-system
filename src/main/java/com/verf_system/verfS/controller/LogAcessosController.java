package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.LogAcessosEntity;
import com.verf_system.verfS.dto.LogAcessosDto;
import com.verf_system.verfS.service.LogAcessosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/LogAcessos")
@RequiredArgsConstructor
public class LogAcessosController {

    private final LogAcessosService logAcessosService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LogAcessosEntity> findAll() {
        return logAcessosService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LogAcessosEntity findById(@PathVariable Long id) {
        return logAcessosService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody LogAcessosDto logAcessosDto) {
        logAcessosService.save(logAcessosDto);
    }
}
