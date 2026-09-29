package com.verf_system.verfS.controller;


import com.verf_system.verfS.dto.request.TintaDto;
import com.verf_system.verfS.dto.response.TintaResponseDto;
import com.verf_system.verfS.service.TintaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/tintas")
@RequiredArgsConstructor
public class TintaController {
    private final TintaService tintaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TintaResponseDto> findAll() {
        return tintaService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TintaResponseDto findById(@PathVariable Long id) {
        return tintaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody TintaDto tintaDto) {
        tintaService.save(tintaDto);
    }


}
