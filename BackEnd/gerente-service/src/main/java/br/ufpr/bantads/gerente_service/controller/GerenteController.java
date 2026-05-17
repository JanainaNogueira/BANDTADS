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

import br.ufpr.bantads.gerente_service.dtos.EditarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.LerGerenteDTO;
import br.ufpr.bantads.gerente_service.service.GerenteService;

@RestController
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService gerenteService;

    public GerenteController(
            GerenteService gerenteService) {

        this.gerenteService = gerenteService;
    }

    @GetMapping
    public ResponseEntity<List<LerGerenteDTO>>
            listar() {

        return ResponseEntity.ok(
                gerenteService.listarTodos()
        );
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<LerGerenteDTO>
            buscarPorCpf(
                    @PathVariable String cpf) {

        return ResponseEntity.ok(
                gerenteService.buscarGerentePorCPF(cpf)
        );
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<LerGerenteDTO>>
            buscarPorNome(
                    @RequestParam String nome) {

        return ResponseEntity.ok(
                gerenteService
                        .buscarGerentePorNome(nome)
        );
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<LerGerenteDTO>
            atualizar(
                    @PathVariable String cpf,

                    @RequestBody
                    EditarGerenteDTO dto) {

        return ResponseEntity.ok(
                gerenteService
                        .atualizar(cpf, dto)
        );
    }

}