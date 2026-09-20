package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.MovimentacaoEstoqueEntity;
import com.verf_system.verfS.database.entity.ProducoesEntity;
import com.verf_system.verfS.database.repository.IEstoqueRepository;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.database.repository.IMovimentacaoEstoqueRepository;
import com.verf_system.verfS.database.repository.IProducoesRepository;
import com.verf_system.verfS.dto.MovimentacaoEstoqueDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {
    private final IMovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final IEstoqueRepository estoqueRepository;
    private final IFuncionarioRepository funcionarioRepository;
    private final IProducoesRepository producoesRepository;

    public void save(MovimentacaoEstoqueDto movimentacaoEstoqueDto) {
        EstoqueEntity estoque = estoqueRepository.findById(movimentacaoEstoqueDto.getIdEstoque()).orElseThrow(() -> new NaoEncontradoException("Nenhum estoque encontrado"));
        FuncionarioEntity funcionario = funcionarioRepository.findById(movimentacaoEstoqueDto.getIdFuncionario()).orElseThrow(() -> new NaoEncontradoException("Nenhum funcionario encontrado"));
        ProducoesEntity producoes = null;
        if(movimentacaoEstoqueDto.getIdProducao()!= null) {
            producoes = producoesRepository.findById(movimentacaoEstoqueDto.getIdProducao()).orElseThrow(() -> new NaoEncontradoException("Nenhuma produção encontrada"));
        }
        movimentacaoEstoqueRepository.save(MovimentacaoEstoqueEntity.builder()
                .estoque(estoque)
                .funcionarioRef(funcionario)
                .producaoRef(producoes)
                .tipoMovimentacao(movimentacaoEstoqueDto.getTipoMovimentacao())
                .quantidadeMovimentada(movimentacaoEstoqueDto.getQuantidadeMovimentada())
                .observacao(movimentacaoEstoqueDto.getObservacao())
                .build());
    }

    public List<MovimentacaoEstoqueEntity> findAll() {
        List<MovimentacaoEstoqueEntity> movimentacoes = movimentacaoEstoqueRepository.findAll();
        if (movimentacoes.isEmpty()) {
            throw new NaoEncontradoException("Nenhuma Movimentacao encontrada");
        }

        return movimentacoes;
    }

    public MovimentacaoEstoqueEntity findById(Long id) {
        MovimentacaoEstoqueEntity movimentacao = movimentacaoEstoqueRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhuma Movimentacao encontrada"));

        return movimentacao;
    }
}
