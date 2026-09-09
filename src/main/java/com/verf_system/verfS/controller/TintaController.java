package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.dto.TintaDto;
import com.verf_system.verfS.service.TintaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/Tinta")
@RequiredArgsConstructor
public class TintaController {

    private final TintaService tintaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TintaEntity> findAll() {
        return tintaService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TintaEntity findById(@PathVariable Integer id) {
        return tintaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody TintaDto tintaDto) {
        tintaService.save(tintaDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TintaEntity update(@PathVariable Integer id, @RequestBody TintaDto tintaDto) {
        return tintaService.update(id, tintaDto);
    }

    @PatchMapping("/{id}/inativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Integer id) {
        tintaService.inativar(id);
    }
}