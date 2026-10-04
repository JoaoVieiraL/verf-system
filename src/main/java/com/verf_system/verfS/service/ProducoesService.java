package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.*;
import com.verf_system.verfS.database.repository.*;
import com.verf_system.verfS.dto.request.ProducoesDto;
import com.verf_system.verfS.dto.response.ProducoesResponseDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducoesService {
    private final IProducoesRepository producoesRepository;
    private final IReceitaRepository receitaRepository;
    private final IEstoqueRepository estoqueRepository;
    private final IItensReceitaRepository itensReceitaRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Transactional
    public void save(ProducoesDto producoesDto) {
        ReceitaEntity receita = receitaRepository.findById(producoesDto.getIdReceita()).orElseThrow(()-> new NaoEncontradoException("Nenhuma receita encontrada"));
        FuncionarioEntity funcionario = (FuncionarioEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ItensReceitaEntity> itensReceita = itensReceitaRepository.findByReceitaRefId(receita.getId());

        BigDecimal soma = BigDecimal.ZERO;

        for (ItensReceitaEntity itensReceitaEntity : itensReceita) {
            BigDecimal proporcao = itensReceitaEntity.getProporcaoPercentual();
            soma = soma.add(proporcao);
        }
        if(soma.compareTo(BigDecimal.valueOf(100)) != 0){
            throw new RegraDeNegocioException("A receita precisa somar 100%. Soma atual: " + soma + "%");
        }


        ProducoesEntity producao = producoesRepository.save(ProducoesEntity.builder()
                .receitaRef(receita)
                .funcionarioRef(funcionario)
                .volumeProduzido(producoesDto.getVolumeProduzido())
                .dataProducao(producoesDto.getDataProducao())
                .build());

        for(ItensReceitaEntity item : itensReceita){
            TintaEntity materiaPrima = item.getTintaMateriaPrimaRef();
            EstoqueEntity estoque = estoqueRepository.findByTintaId(materiaPrima.getId()).orElseThrow(()-> new NaoEncontradoException("Sem estoque para "+materiaPrima.getNome()));

            BigDecimal proporcao = item.getProporcaoPercentual();

            BigDecimal volumeProduzido = BigDecimal.valueOf(producao.getVolumeProduzido());

            BigDecimal quantidadeCalculada = proporcao.multiply(volumeProduzido);

            BigDecimal quantidadeNecessaria = quantidadeCalculada.divide(
                    BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

            int necessario = quantidadeNecessaria.intValue();

            if (estoque.getQuantidade() < necessario) {

                throw new RegraDeNegocioException(
                        "Estoque insuficiente de " + materiaPrima.getNome() + ": precisa " + necessario + " mL, tem " + estoque.getQuantidade() + " mL");
            }

            movimentacaoEstoqueService.registrar(
                    estoque,
                    TipoMovimentacao.SAIDA,
                    MotivoMovimentacao.CONSUMIDA,
                    necessario,
                    null,
                    producao,
                    funcionario
            );


        }
        EstoqueEntity estoqueProduzido = estoqueRepository.findByTintaId(receita.getTintaResultante().getId())
                .orElseThrow(() -> new NaoEncontradoException("Sem estoque para a tinta produzida"));
        movimentacaoEstoqueService.registrar(estoqueProduzido, TipoMovimentacao.ENTRADA,
                MotivoMovimentacao.FABRICADA, producao.getVolumeProduzido(), null, producao, funcionario);
    }

    public List<ProducoesResponseDto> findAll() {
        List<ProducoesEntity> producoes = producoesRepository.findAll();


        List<ProducoesResponseDto> producaoResponses = new ArrayList<>();
        for (ProducoesEntity producao : producoes) {
            producaoResponses.add(new ProducoesResponseDto(producao));
        }

        return producaoResponses;
    }

    public ProducoesResponseDto findById(Long id) {
        ProducoesEntity producao = producoesRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhuma Producao encontrada"));

        return new ProducoesResponseDto(producao);
    }
}
