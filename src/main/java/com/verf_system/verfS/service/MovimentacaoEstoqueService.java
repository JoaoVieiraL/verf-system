package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.MovimentacaoEstoqueEntity;
import com.verf_system.verfS.database.repository.IMovimentacaoEstoqueRepository;
import com.verf_system.verfS.dto.MovimentacaoEstoqueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {
    private final IMovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public void save(MovimentacaoEstoqueDto movimentacaoEstoqueDto) {
        movimentacaoEstoqueRepository.save(MovimentacaoEstoqueEntity.builder()
                .estoque(movimentacaoEstoqueDto.getEstoque())
                .funcionarioRef(movimentacaoEstoqueDto.getFuncionarioRef())
                .producaoRef(movimentacaoEstoqueDto.getProducaoRef())
                .tipoMovimentacao(movimentacaoEstoqueDto.getTipoMovimentacao())
                .quantidadeAnterior(movimentacaoEstoqueDto.getQuantidadeAnterior())
                .quantidadeMovimentada(movimentacaoEstoqueDto.getQuantidadeMovimentada())
                .quantidadePosterior(movimentacaoEstoqueDto.getQuantidadePosterior())
                .observacao(movimentacaoEstoqueDto.getObservacao())
                .dataMovimentacao(movimentacaoEstoqueDto.getDataMovimentacao())
                .build());
    }

    public List<MovimentacaoEstoqueEntity> findAll() {
        List<MovimentacaoEstoqueEntity> movimentacoes = movimentacaoEstoqueRepository.findAll();
        if (movimentacoes.isEmpty()) {
            throw new RuntimeException("Nenhuma Movimentacao encontrada");
        }

        return movimentacoes;
    }

    public MovimentacaoEstoqueEntity findById(Long id) {
        MovimentacaoEstoqueEntity movimentacao = movimentacaoEstoqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhuma Movimentacao encontrada"));

        return movimentacao;
    }
}
