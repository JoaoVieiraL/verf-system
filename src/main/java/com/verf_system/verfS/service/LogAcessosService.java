package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.LogAcessosEntity;
import com.verf_system.verfS.database.repository.ILogAcessosRepository;
import com.verf_system.verfS.dto.LogAcessosDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogAcessosService {
    private final ILogAcessosRepository logAcessosRepository;

    public void save(LogAcessosDto logAcessosDto) {
        logAcessosRepository.save(LogAcessosEntity.builder()
                .usuario(logAcessosDto.getUsuario())
                .emailUsado(logAcessosDto.getEmailUsado())
                .sucesso(logAcessosDto.isSucesso())
                .ipOrigem(logAcessosDto.getIpOrigem())
                .datahora(LocalDateTime.now())
                .build());
    }

    public List<LogAcessosEntity> findAll() {
        List<LogAcessosEntity> logs = logAcessosRepository.findAll();
        if (logs.isEmpty()) {
            throw new RuntimeException("Nenhum Log de Acesso encontrado");
        }

        return logs;
    }

    public LogAcessosEntity findById(Long id) {
        LogAcessosEntity log = logAcessosRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum Log de Acesso encontrado"));

        return log;
    }
}
