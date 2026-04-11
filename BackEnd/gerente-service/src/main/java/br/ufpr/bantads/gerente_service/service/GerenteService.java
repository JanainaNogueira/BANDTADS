package br.ufpr.bantads.gerente_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.LerGerenteDTO;
import br.ufpr.bantads.gerente_service.model.Gerente;
import br.ufpr.bantads.gerente_service.repository.GerenteRepository;

@Service
public class GerenteService {
   @Autowired
   private GerenteRepository gerenteRepository;

    public List<LerGerenteDTO> listarTodos() {
        return gerenteRepository.findAll()
            .stream()
            .map(g -> new LerGerenteDTO(
                g.getNome(),
                g.getEmail()
            ))
            .toList();
    }

    public Gerente buscarGerentePorId(Integer id) {
        return gerenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
    }

    public List<Gerente> buscarGerentePorNome(String nome) {
        return gerenteRepository.findByNomeContainingIgnoreCase(nome);
    }

   public void criar(AdicionarGerenteDTO gerenteDto) {
        Gerente gerente = new Gerente();
        gerente.setNome(gerenteDto.nome());
        gerente.setCpf(gerenteDto.cpf());
        gerente.setTelefone(gerenteDto.telefone());
        gerente.setEmail(gerenteDto.email());
        gerente.setSenha(gerenteDto.senha());

        gerente = gerenteRepository.save(gerente);
    }

    public Gerente atualizar(Integer id, Gerente gerenteAtualizado) {
        Gerente gerente = buscarGerentePorId(id);
        return gerenteRepository.save(gerente);
    }

    public void deletar(Integer id) {
        Gerente gerente = buscarGerentePorId(id);
        gerenteRepository.delete(gerente);
    }
}