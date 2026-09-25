package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.LogAcessosEntity;
import com.verf_system.verfS.database.entity.UsuarioEntity;
import com.verf_system.verfS.database.repository.ILogAcessosRepository;
import com.verf_system.verfS.database.repository.IUsuarioRepository;
import com.verf_system.verfS.dto.request.LogAcessosDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
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

    public List<LogAcessosEntity> findAll() {
        List<LogAcessosEntity> logs = logAcessosRepository.findAll();
        if (logs.isEmpty()) {
            throw new NaoEncontradoException("Nenhum Log de Acesso encontrado");
        }

        return logs;
    }

    public LogAcessosEntity findById(Long id) {
        LogAcessosEntity log = logAcessosRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Log de Acesso encontrado"));

        return log;
    }
}
