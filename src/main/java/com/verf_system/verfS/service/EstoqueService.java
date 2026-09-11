package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.database.repository.IEstoqueRepository;
import com.verf_system.verfS.dto.EstoqueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {
    private final IEstoqueRepository estoqueRepository;

    public void save(EstoqueDto estoqueDto) {
        estoqueRepository.save(EstoqueEntity.builder()
                .tinta(estoqueDto.getTintaRef())
                .quantidade(estoqueDto.getQuantidade())
                .build());
    }

    public List<EstoqueEntity> findAll() {
        List<EstoqueEntity> estoques = estoqueRepository.findAll();
        if (estoques.isEmpty()) {
            throw new RuntimeException("Nenhum Estoque encontrado");
        }

        return estoques;
    }

    public EstoqueEntity findById(Long id) {
        EstoqueEntity estoque = estoqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum Estoque encontrado"));

        return estoque;
    }
}
