package br.ufpr.bantads.gerente_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.EditarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.LerGerenteDTO;
import br.ufpr.bantads.gerente_service.model.GerenteAdmin;
import br.ufpr.bantads.gerente_service.model.TipoUsuario;
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
                g.getCpf(),
                g.getEmail(),
                g.getTelefone()
            ))
            .toList();
    }

   public LerGerenteDTO buscarGerentePorId(Integer id) {
        GerenteAdmin gerente = gerenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        return new LerGerenteDTO(
            gerente.getNome(),
            gerente.getCpf(),
            gerente.getEmail(),
            gerente.getTelefone()
        );
    }
    
    public List<LerGerenteDTO> buscarGerentePorNome(String nome) {
        return gerenteRepository.findByNomeContainingIgnoreCase(nome)
        .stream()
        .map(g -> new LerGerenteDTO(
            g.getNome(),
            g.getCpf(),
            g.getEmail(),
            g.getTelefone()
        ))
        .toList();
    }

  public LerGerenteDTO buscarGerentePorCPF(String cpf) {
        GerenteAdmin gerente = gerenteRepository.findByCpf(cpf)
            .orElseThrow(() -> new RuntimeException("Gerente com cpf: " + cpf + " não encontrado"));

        return new LerGerenteDTO(
            gerente.getNome(),
            gerente.getCpf(),
            gerente.getEmail(),
            gerente.getTelefone()
        );
    }

   public GerenteAdmin criarGerente(AdicionarGerenteDTO gerenteDto) {
        GerenteAdmin gerente = new GerenteAdmin();
        gerente.setNome(gerenteDto.nome());
        gerente.setCpf(gerenteDto.cpf());
        gerente.setTelefone(gerenteDto.telefone());
        gerente.setEmail(gerenteDto.email());
        gerente.setSenha(gerenteDto.senha());
        gerente.setTipoUsuario(TipoUsuario.GERENTE);

        gerente = gerenteRepository.save(gerente);

        return gerente;
    }

    public LerGerenteDTO atualizar(Integer id, EditarGerenteDTO dto) {
        GerenteAdmin gerente = gerenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            gerente.setNome(dto.nome());
        }

        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            gerente.setTelefone(dto.telefone());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            gerente.setEmail(dto.email());
        }

        if (dto.senha() != null && !dto.senha().isBlank()) {
            gerente.setSenha(dto.senha());
        }

        gerente = gerenteRepository.save(gerente);

        return new LerGerenteDTO(
            gerente.getNome(),
            gerente.getEmail(),
            gerente.getCpf(),
            gerente.getTelefone()
        );
    }

    public void RemoverGerenteCompensacao(Integer id) {
        GerenteAdmin gerente = gerenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        gerenteRepository.delete(gerente);
    }

    public void removerGerente(Integer idGerente) {
        gerenteRepository.deleteById(idGerente);
    }
}