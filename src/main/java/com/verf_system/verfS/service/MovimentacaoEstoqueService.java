package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.*;
import com.verf_system.verfS.database.repository.IEstoqueRepository;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.database.repository.IMovimentacaoEstoqueRepository;
import com.verf_system.verfS.database.repository.IProducoesRepository;
import com.verf_system.verfS.dto.request.MovimentacaoEstoqueDto;
import com.verf_system.verfS.dto.response.MovimentacaoEstoqueResponseDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {
    private final IMovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final IEstoqueRepository estoqueRepository;
    private final IFuncionarioRepository funcionarioRepository;
    private final IProducoesRepository producoesRepository;

    @Transactional
    public void save(MovimentacaoEstoqueDto movimentacaoEstoqueDto) {
        if(!movimentacaoEstoqueDto.getMotivo().aceita(movimentacaoEstoqueDto.getTipoMovimentacao())){
            throw new RegraDeNegocioException("Motivo de movimentação não é compatível com o tipo de movimentação");
        }

        EstoqueEntity estoque = estoqueRepository.findById(movimentacaoEstoqueDto.getIdEstoque()).orElseThrow(() -> new NaoEncontradoException("Nenhum estoque encontrado"));
        FuncionarioEntity funcionario = funcionarioRepository.findById(movimentacaoEstoqueDto.getIdFuncionario()).orElseThrow(() -> new NaoEncontradoException("Nenhum funcionario encontrado"));
        ProducoesEntity producoes = null;
        if (movimentacaoEstoqueDto.getIdProducao() != null) {
            producoes = producoesRepository.findById(movimentacaoEstoqueDto.getIdProducao()).orElseThrow(() -> new NaoEncontradoException("Nenhuma produção encontrada"));
        }
        Integer qtdAnterior = estoque.getQuantidade();
        Integer qtdPosterior;

        switch (movimentacaoEstoqueDto.getTipoMovimentacao()) {
            case ENTRADA:
                qtdPosterior = qtdAnterior + movimentacaoEstoqueDto.getQuantidadeMovimentada();
                estoque.setQuantidade(qtdPosterior);
                break;
            case SAIDA:
                if (qtdAnterior <= 0) {
                    throw new RegraDeNegocioException("Não é possivel realizar movimentos SAIDA com a quantidade atual em estoque");
                } else if (qtdAnterior < movimentacaoEstoqueDto.getQuantidadeMovimentada()) {
                    throw new RegraDeNegocioException("Quantidade movimentada nao pode ser maior que a quantidade disponivel no estoque");
                }
                qtdPosterior = qtdAnterior - movimentacaoEstoqueDto.getQuantidadeMovimentada();
                estoque.setQuantidade(qtdPosterior);
                break;


        }

        movimentacaoEstoqueRepository.save(MovimentacaoEstoqueEntity.builder()
                .estoque(estoque)
                .funcionarioRef(funcionario)
                .producaoRef(producoes)
                .tipoMovimentacao(movimentacaoEstoqueDto.getTipoMovimentacao())
                .quantidadeAnterior(qtdAnterior)
                .quantidadePosterior(estoque.getQuantidade())
                .quantidadeMovimentada(movimentacaoEstoqueDto.getQuantidadeMovimentada())
                .observacao(movimentacaoEstoqueDto.getObservacao())
                .motivo(movimentacaoEstoqueDto.getMotivo())
                .build());
    }

    public List<MovimentacaoEstoqueResponseDto> findAll() {
        List<MovimentacaoEstoqueEntity> movimentacoes = movimentacaoEstoqueRepository.findAll();


        List<MovimentacaoEstoqueResponseDto> movimentacaoResponses = new ArrayList<>();
        for (MovimentacaoEstoqueEntity movimentacao : movimentacoes) {
            movimentacaoResponses.add(new MovimentacaoEstoqueResponseDto(movimentacao));
        }

        return movimentacaoResponses;
    }

    public MovimentacaoEstoqueResponseDto findById(Long id) {
        MovimentacaoEstoqueEntity movimentacao = movimentacaoEstoqueRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhuma Movimentacao encontrada"));

        return new MovimentacaoEstoqueResponseDto(movimentacao);
    }

    @Transactional
    public void registrar(
            EstoqueEntity estoque,
            TipoMovimentacao tipo,
            MotivoMovimentacao motivo,
            Integer quantidade,
            String observacao,
            ProducoesEntity producao,
            FuncionarioEntity funcionario) {

        if (!motivo.aceita(tipo)) {
            throw new RegraDeNegocioException("Motivo de movimentação não é compatível com o tipo de movimentação");
        }

        Integer quantidadeAnterior = estoque.getQuantidade();

        Integer quantidadePosterior;

        if (tipo == TipoMovimentacao.ENTRADA) {

            quantidadePosterior = quantidadeAnterior + quantidade;

        } else {

            if (quantidade > quantidadeAnterior) {
                throw new RegraDeNegocioException("Quantidade movimentada não pode ser maior que a quantidade disponível no estoque");
            }

            quantidadePosterior = quantidadeAnterior - quantidade;
        }

        estoque.setQuantidade(quantidadePosterior);

        MovimentacaoEstoqueEntity movimentacao =
                MovimentacaoEstoqueEntity.builder()
                        .estoque(estoque)
                        .funcionarioRef(funcionario)
                        .producaoRef(producao)
                        .tipoMovimentacao(tipo)
                        .motivo(motivo)
                        .quantidadeAnterior(quantidadeAnterior)
                        .quantidadeMovimentada(quantidade)
                        .quantidadePosterior(quantidadePosterior)
                        .observacao(observacao)
                        .build();

        movimentacaoEstoqueRepository.save(movimentacao);
    }

}
