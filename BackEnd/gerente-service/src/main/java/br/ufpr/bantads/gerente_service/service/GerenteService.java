package br.ufpr.bantads.gerente_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.EditarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.LerGerenteDTO;
import br.ufpr.bantads.gerente_service.messaging.GerenteProducer;
import br.ufpr.bantads.gerente_service.model.GerenteAdmin;
import br.ufpr.bantads.gerente_service.model.TipoUsuario;
import br.ufpr.bantads.gerente_service.repository.GerenteRepository;

@Service
public class GerenteService {

    @Autowired
    private GerenteRepository gerenteRepository;

    @Autowired
    private GerenteProducer producer;


    public GerenteService(GerenteRepository gerenteRepository, GerenteProducer producer) {
        this.gerenteRepository = gerenteRepository;
        this.producer = producer;
    }

    public List<LerGerenteDTO> listarTodos() {
        return gerenteRepository.findAll()
                .stream()
                .map(g -> new LerGerenteDTO(
                g.getId(),
                g.getNome(),
                g.getCpf(),
                g.getEmail(),
                g.getTipoUsuario().name(),
                g.getTelefone()
        ))
                .toList();
    }

    public LerGerenteDTO buscarGerentePorId(Integer id) {
        GerenteAdmin gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        return new LerGerenteDTO(
                gerente.getId(),
                gerente.getNome(),
                gerente.getCpf(),
                gerente.getEmail(),
                gerente.getTipoUsuario().name(),
                gerente.getTelefone()
        );
    }

    public List<LerGerenteDTO> buscarGerentePorNome(String nome) {
        return gerenteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(g -> new LerGerenteDTO(
                g.getId(),
                g.getNome(),
                g.getCpf(),
                g.getEmail(),
                g.getTipoUsuario().name(),
                g.getTelefone()
        ))
                .toList();
    }

    public LerGerenteDTO buscarGerentePorCPF(String cpf) {
        GerenteAdmin gerente = gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "Gerente com cpf: " + cpf + " não encontrado"
                ));

        return new LerGerenteDTO(
                gerente.getId(),
                gerente.getNome(),
                gerente.getCpf(),
                gerente.getEmail(),
                gerente.getTipoUsuario().name(),
                gerente.getTelefone()
        );
    }

    public GerenteAdmin buscarGerenteAdminPorCPF(String cpf) {
        return gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "Gerente com cpf: " + cpf + " não encontrado"
                ));
    }

    public GerenteAdmin criarGerenteInterno(AdicionarGerenteDTO gerenteDto) {

        if (gerenteRepository.findByCpf(gerenteDto.cpf()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        GerenteAdmin gerente = new GerenteAdmin();
        gerente.setNome(gerenteDto.nome());
        gerente.setCpf(gerenteDto.cpf());
        gerente.setEmail(gerenteDto.email());
        gerente.setSenha(gerenteDto.senha());
        gerente.setTipoUsuario(TipoUsuario.GERENTE);

        if (gerenteDto.telefone() == null || gerenteDto.telefone().isEmpty()) {
            gerente.setTelefone("");
        } else {
            gerente.setTelefone(gerenteDto.telefone());
        }

        return gerenteRepository.save(gerente);
    }

    public LerGerenteDTO atualizar(String cpf, EditarGerenteDTO dto) {
        GerenteAdmin gerente = gerenteRepository.findByCpf(cpf)
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
                gerente.getId(),
                gerente.getNome(),
                gerente.getCpf(),
                gerente.getEmail(),
                gerente.getTipoUsuario().name(),
                gerente.getTelefone()
        );
    }

    public void removerGerentePorCpf(String cpf) {
        GerenteAdmin gerente = gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        gerenteRepository.delete(gerente);
    }
}
