package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.ProducoesEntity;
import com.verf_system.verfS.database.repository.IProducoesRepository;
import com.verf_system.verfS.dto.ProducoesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducoesService {
    private final IProducoesRepository producoesRepository;

    public void save(ProducoesDto producoesDto) {
        producoesRepository.save(ProducoesEntity.builder()
                .receitaRef(producoesDto.getReceitaRef())
                .funcionarioRef(producoesDto.getFuncionarioRef())
                .volumeProduzido(producoesDto.getVolumeProduzido())
                .dataProducao(producoesDto.getDataProducao())
                .criadoEm(LocalDateTime.now())
                .build());
    }

    public List<ProducoesEntity> findAll() {
        List<ProducoesEntity> producoes = producoesRepository.findAll();
        if (producoes.isEmpty()) {
            throw new RuntimeException("Nenhuma Producao encontrada");
        }

        return producoes;
    }

    public ProducoesEntity findById(Long id) {
        ProducoesEntity producao = producoesRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhuma Producao encontrada"));

        return producao;
    }
}
