package com.verf_system.verfS.controller;

import com.verf_system.verfS.database.entity.UsuarioEntity;
import com.verf_system.verfS.dto.UsuarioDto;
import com.verf_system.verfS.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/Usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UsuarioEntity> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioEntity findById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody UsuarioDto usuarioDto) {
        usuarioService.save(usuarioDto);
    }

    @DeleteMapping(value = "ID/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
    }
}
