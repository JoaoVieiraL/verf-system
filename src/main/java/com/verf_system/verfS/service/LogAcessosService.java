package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.LogAcessosEntity;
import com.verf_system.verfS.database.entity.UsuarioEntity;
import com.verf_system.verfS.database.repository.ILogAcessosRepository;
import com.verf_system.verfS.database.repository.IUsuarioRepository;
import com.verf_system.verfS.dto.LogAcessosDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogAcessosService {
    private final ILogAcessosRepository logAcessosRepository;
    private final IUsuarioRepository usuarioRepository;

    public void save(LogAcessosDto logAcessosDto) {
        UsuarioEntity usuario = null;
        if (logAcessosDto.getIdUsuario() != null) {
            usuario = usuarioRepository.findById(logAcessosDto.getIdUsuario()).orElseThrow(() -> new RuntimeException("Nenhum Usuario encontrado"));
        }
        logAcessosRepository.save(LogAcessosEntity.builder()
                .usuario(usuario)
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
