package com.genial.genial.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import com.genial.genial.model.Pessoa;
import com.genial.genial.model.exception.ResourceNotFound;
import com.genial.genial.repository.PessoaRepository;
import com.genial.genial.shared.PessoaDTO;

@Service
public class PessoaService {
    
    @Autowired
    private PessoaRepository pessoaRepository;

    public List<PessoaDTO> obterTodos()
    {
        List<Pessoa> pessoas = pessoaRepository.findAll();

        return pessoas.stream()
        .map(pessoa -> new ModelMapper().map(pessoa, PessoaDTO.class))
        .collect(Collectors.toList());
    }

    public Optional<PessoaDTO> obterPorId(Integer id)
    {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);
        if(pessoa.isEmpty())
        {
            throw new ResourceNotFound("Pessoa com id: " + id + " não encontrado");
        }
        
        PessoaDTO dto = new ModelMapper().map(pessoa.get(), PessoaDTO.class);

        return Optional.of(dto);

    }

    public PessoaDTO adicionar(PessoaDTO pessoaDto)
    {
        pessoaDto.setId(null);

        ModelMapper mapper = new ModelMapper();

        Pessoa pessoa = mapper.map(pessoaDto, Pessoa.class);

        pessoa = pessoaRepository.save(pessoa);

        pessoaDto.setId(pessoa.getId());

        return pessoaDto;
    }

    public void deletar(Integer id)
    {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);

        if(pessoa.isEmpty())
        {
            throw new ResourceNotFound("Não foi possivel deletar o pessoa com o id: " + id + " - pessoa não encontrada");
        }
        pessoaRepository.deleteById(id);
    }

    public PessoaDTO atualizar(Integer id, PessoaDTO pessoaDto)
    {
        pessoaDto.setId(id);

        ModelMapper mapper = new ModelMapper();

        Pessoa pessoa = mapper.map(pessoaDto, Pessoa.class);

        pessoaRepository.save(pessoa);

        return pessoaDto;
    }

}
