package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.IEstoqueRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.request.EstoqueRequestDto;
import com.verf_system.verfS.dto.request.EstoqueUpdateDto;
import com.verf_system.verfS.dto.response.EstoqueResponseDto;
import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {
    private final IEstoqueRepository estoqueRepository;
    private final ITintaRepository tintaRepository;

    public void save(EstoqueRequestDto estoqueDto) {

        TintaEntity tinta = tintaRepository.findById(estoqueDto.getIdTinta()).orElseThrow(()-> new NaoEncontradoException("Nenhuma tinta encontrada"));
        if(estoqueRepository.findByTintaId(tinta.getId()).isPresent()){
            throw new DadoDuplicadoException("A tinta " + tinta.getNome() + " já possui estoque cadastrado. Edite a quantidade existente.");
        }

        estoqueRepository.save(EstoqueEntity.builder()
                .tinta(tinta)
                .quantidade(estoqueDto.getQuantidade())
                .build());
    }

    public List<EstoqueResponseDto> findAll() {
        List<EstoqueEntity> estoques = estoqueRepository.findAll();

        List<EstoqueResponseDto> estoqueResponses = new ArrayList<>();
        for (EstoqueEntity estoque : estoques) {
            estoqueResponses.add(new EstoqueResponseDto(estoque));
        }

        return estoqueResponses;
    }

    public void atualizar(Long id, EstoqueUpdateDto dados) {
        EstoqueEntity estoque = estoqueRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Estoque encontrado"));
        estoque.setQuantidade(dados.getQuantidade());
        estoqueRepository.save(estoque);
    }

    public void excluir(Long id) {
        EstoqueEntity estoque = estoqueRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Estoque encontrado"));
        try {
            estoqueRepository.delete(estoque);
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException("Este estoque possui movimentações registradas e não pode ser excluído.");
        }
    }

    public EstoqueResponseDto findById(Long id) {
        EstoqueEntity estoque = estoqueRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Estoque encontrado"));

        return new EstoqueResponseDto(estoque);
    }
}
