package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.*;
import com.verf_system.verfS.database.repository.IEstoqueRepository;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.database.repository.IMovimentacaoEstoqueRepository;
import com.verf_system.verfS.dto.request.MovimentacaoEstoqueDto;
import com.verf_system.verfS.dto.response.MovimentacaoEstoqueResponseDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {
    private final IMovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final IEstoqueRepository estoqueRepository;
    private final IFuncionarioRepository funcionarioRepository;
    private static final Set<MotivoMovimentacao> MOTIVOS_DA_PRODUCAO =
            EnumSet.of(MotivoMovimentacao.FABRICADA, MotivoMovimentacao.CONSUMIDA);

    @Transactional
    public MovimentacaoEstoqueResponseDto save(MovimentacaoEstoqueDto movimentacaoEstoqueDto) {
        if (MOTIVOS_DA_PRODUCAO.contains(movimentacaoEstoqueDto.getMotivo())) {
            throw new RegraDeNegocioException("Movimentações de produção são geradas automaticamente ao registrar uma produção");
        }

        FuncionarioEntity funcionario = (FuncionarioEntity)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (movimentacaoEstoqueDto.getMotivo() == MotivoMovimentacao.AJUSTE) {
            boolean ehGerente = funcionario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("NIVEL_GERENTE"));
            if (!ehGerente) {
                throw new AccessDeniedException("Ajuste de estoque exige nível GERENTE");
            }
            if (movimentacaoEstoqueDto.getObservacao() == null || movimentacaoEstoqueDto.getObservacao().isBlank()) {
                throw new RegraDeNegocioException("Ajuste exige uma justificativa no campo observacao");
            }
        }

        EstoqueEntity estoque = estoqueRepository.findById(movimentacaoEstoqueDto.getIdEstoque()).orElseThrow(() -> new NaoEncontradoException("Nenhum estoque encontrado"));

        MovimentacaoEstoqueEntity movimentacao = registrar(
                estoque,
                movimentacaoEstoqueDto.getTipoMovimentacao(),
                movimentacaoEstoqueDto.getMotivo(),
                movimentacaoEstoqueDto.getQuantidadeMovimentada(),
                movimentacaoEstoqueDto.getObservacao(),
                null,
                funcionario);

        return new MovimentacaoEstoqueResponseDto(movimentacao);
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
    public MovimentacaoEstoqueEntity registrar(
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

        return movimentacaoEstoqueRepository.save(movimentacao);
    }

}
