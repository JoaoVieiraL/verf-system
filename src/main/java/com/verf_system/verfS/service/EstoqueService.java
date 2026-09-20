package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.IEstoqueRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.EstoqueDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {
    private final IEstoqueRepository estoqueRepository;
    private final ITintaRepository tintaRepository;

    public void save(EstoqueDto estoqueDto) {

        TintaEntity tinta = tintaRepository.findById(estoqueDto.getIdTinta()).orElseThrow(()-> new NaoEncontradoException("Nenhuma tinta encontrada"));

        estoqueRepository.save(EstoqueEntity.builder()
                .tinta(tinta)
                .quantidade(estoqueDto.getQuantidade())
                .build());
    }

    public List<EstoqueEntity> findAll() {
        List<EstoqueEntity> estoques = estoqueRepository.findAll();
        if (estoques.isEmpty()) {
            throw new NaoEncontradoException("Nenhum Estoque encontrado");
        }

        return estoques;
    }

    public EstoqueEntity findById(Long id) {
        EstoqueEntity estoque = estoqueRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Estoque encontrado"));

        return estoque;
    }
}
