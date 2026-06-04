package br.ufpr.bantads.gerente_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.gerente_service.model.GerenteAdmin;
import br.ufpr.bantads.gerente_service.model.TipoUsuario;
import br.ufpr.bantads.gerente_service.repository.GerenteRepository;

@RestController
public class RebootController {

    private final GerenteRepository repo;

    public RebootController(GerenteRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {

        repo.deleteAll();

        GerenteAdmin g1 = new GerenteAdmin();
        g1.setNome("Geniéve");
        g1.setCpf("98574307084");
        g1.setTelefone("11999999999");
        g1.setEmail("ger1@bantads.com.br");
        g1.setSenha("tads");
        g1.setTipoUsuario(TipoUsuario.GERENTE);

        GerenteAdmin g2 = new GerenteAdmin();
        g2.setNome("Godophredo");
        g2.setCpf("64065268052");
        g2.setTelefone("11888888888");
        g2.setEmail("ger2@bantads.com.br");
        g2.setSenha("tads");
        g2.setTipoUsuario(TipoUsuario.GERENTE);

        GerenteAdmin g3 = new GerenteAdmin();
        g3.setNome("Gyândula");
        g3.setCpf("23862179060");
        g3.setTelefone("11777777777");
        g3.setEmail("ger3@bantads.com.br");
        g3.setSenha("tads");
        g3.setTipoUsuario(TipoUsuario.GERENTE);

        repo.save(g1);
        repo.save(g2);
        repo.save(g3);

        return ResponseEntity.ok().build();
    }
}