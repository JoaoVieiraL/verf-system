package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.LogAcessosEntity;
import com.verf_system.verfS.database.entity.UsuarioEntity;
import com.verf_system.verfS.database.repository.ILogAcessosRepository;
import com.verf_system.verfS.database.repository.IUsuarioRepository;
import com.verf_system.verfS.dto.request.LogAcessosDto;
import com.verf_system.verfS.dto.response.LogAcessosResponseDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogAcessosService {
    private final ILogAcessosRepository logAcessosRepository;
    private final IUsuarioRepository usuarioRepository;

    public void save(LogAcessosDto logAcessosDto) {
        UsuarioEntity usuario = null;
        if (logAcessosDto.getIdUsuario() != null) {
            usuario = usuarioRepository.findById(logAcessosDto.getIdUsuario()).orElseThrow(() -> new NaoEncontradoException("Nenhum Usuario encontrado"));
        }
        logAcessosRepository.save(LogAcessosEntity.builder()
                .usuario(usuario)
                .emailUsado(logAcessosDto.getEmailUsado())
                .sucesso(logAcessosDto.isSucesso())
                .ipOrigem(logAcessosDto.getIpOrigem())
                .datahora(LocalDateTime.now())
                .build());
    }

    // Grava uma tentativa de login (com ou sem sucesso). Os limites seguem as colunas da tabela.
    public void registrarLogin(String email, boolean sucesso, String ip) {
        logAcessosRepository.save(LogAcessosEntity.builder()
                .emailUsado(limitar(email == null ? "" : email, 120))
                .sucesso(sucesso)
                .ipOrigem(limitar(ip == null ? "desconhecido" : ip, 40))
                .datahora(LocalDateTime.now())
                .build());
    }

    private String limitar(String texto, int tamanho) {
        return texto.length() > tamanho ? texto.substring(0, tamanho) : texto;
    }

    public List<LogAcessosResponseDto> findAll() {
        List<LogAcessosEntity> logs = logAcessosRepository.findAll();


        List<LogAcessosResponseDto> logResponses = new ArrayList<>();
        for (LogAcessosEntity log : logs) {
            logResponses.add(new LogAcessosResponseDto(log));
        }

        return logResponses;
    }

    public LogAcessosResponseDto findById(Long id) {
        LogAcessosEntity log = logAcessosRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Log de Acesso encontrado"));

        return new LogAcessosResponseDto(log);
    }
}
