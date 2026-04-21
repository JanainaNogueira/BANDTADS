package br.ufpr.bantads.gerente_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.LerGerenteDTO;
import br.ufpr.bantads.gerente_service.model.Gerente;
import br.ufpr.bantads.gerente_service.read.model.GerenteClientesView;
import br.ufpr.bantads.gerente_service.read.repository.GerenteClientesRepository;
import br.ufpr.bantads.gerente_service.service.GerenteService;

@RestController
@RequestMapping("/gerentes")
public class GerenteController {

    @Autowired
    private GerenteService gerenteService;

    @Autowired
    private GerenteClientesRepository gerenteClientesRepository;

    @GetMapping
    public ResponseEntity<List<LerGerenteDTO>> listar() {
        return ResponseEntity.ok(gerenteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gerente> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(gerenteService.buscarGerentePorId(id));
    }

    @GetMapping("/{id}/clientes")
    public ResponseEntity<GerenteClientesView> buscarComClientes(@PathVariable Integer id) {
        return ResponseEntity.ok(
            gerenteClientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não encontrado"))
        );
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Gerente>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(gerenteService.buscarGerentePorNome(nome));
    }

    @PostMapping
    public ResponseEntity<Gerente> criar(@RequestBody AdicionarGerenteDTO dto) {
        Gerente gerente = gerenteService.criar(dto);
        return ResponseEntity.ok(gerente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gerente> atualizar(
            @PathVariable Integer id,
            @RequestBody Gerente gerenteAtualizado) {

        return ResponseEntity.ok(gerenteService.atualizar(id, gerenteAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        gerenteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}