package com.genial.genial.view.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genial.genial.services.PessoaService;
import com.genial.genial.shared.PessoaDTO;
import com.genial.genial.view.model.PessoaRequest;
import com.genial.genial.view.model.PessoaResponse;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {
    
    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaResponse>> obterTodos()
    {
        List<PessoaDTO> pessoas = pessoaService.obterTodos();

        ModelMapper mapper = new ModelMapper();

        List<PessoaResponse> resposta = pessoas
        .stream()
        .map(pessoa -> mapper.map(pessoa, PessoaResponse.class))
        .collect(Collectors.toList());

        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PessoaResponse> adicionar(@RequestBody PessoaRequest pessoaRequest)
    {
        ModelMapper mapper = new ModelMapper();
        PessoaDTO dto = mapper.map(pessoaRequest, PessoaDTO.class);
        dto = pessoaService.adicionar(dto);
        return new ResponseEntity<>(mapper.map(dto, PessoaResponse.class), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PessoaResponse>> obterPorId(@PathVariable Integer id)
    {
        Optional<PessoaDTO> dto = pessoaService.obterPorId(id);

        PessoaResponse pessoa = new ModelMapper().map(dto.get(), PessoaResponse.class);

        return new ResponseEntity<>(Optional.of(pessoa), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id)
    {
        pessoaService.deletar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{id}")
    public ResponseEntity<PessoaResponse> atualizar(@RequestBody PessoaRequest pessoaRequest, @PathVariable Integer id)
    {
        ModelMapper mapper = new ModelMapper();

        PessoaDTO dto = mapper.map(pessoaRequest, PessoaDTO.class);
        dto = pessoaService.atualizar(id, dto);
        return new ResponseEntity<>(
            mapper.map(dto, PessoaResponse.class),
            HttpStatus.OK
        );
    }

}
