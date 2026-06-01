package br.ufpr.bantads.auth_service.config;

import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o banco está vazio para evitar duplicidade
        if (usuarioRepository.count() == 0) {
            
            // Hash SHA-256 para a senha "tads" concatenada com o salt "tads" ("tadstads")
            String senhaHash = "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b";

            // Administrador
            usuarioRepository.save(new Usuario(
                    null,
                    "adm1@bantads.com.br",
                    senhaHash,
                    "ADMINISTRADOR",
                    "40501740066"
            ));

            // Gerentes
            usuarioRepository.save(new Usuario(
                    null,
                    "ger1@bantads.com.br",
                    senhaHash,
                    "GERENTE",
                    "98574307084"   // Geniéve
            ));

            usuarioRepository.save(new Usuario(
                    null,
                    "ger2@bantads.com.br",
                    senhaHash,
                    "GERENTE",
                    "64065268052"   // Godophredo
            ));

            usuarioRepository.save(new Usuario(
                    null,
                    "ger3@bantads.com.br",
                    senhaHash,
                    "GERENTE",
                    "23862179060"   // Gyândula
            ));

            // Clientes
            usuarioRepository.save(new Usuario(
                    null,
                    "cli1@bantads.com.br",
                    senhaHash,
                    "CLIENTE",
                    "12912861012"   // Catharyna
            ));

            usuarioRepository.save(new Usuario(
                    null,
                    "cli2@bantads.com.br",
                    senhaHash,
                    "CLIENTE",
                    "09506382000"   // Cleuddônio
            ));

            usuarioRepository.save(new Usuario(
                    null,
                    "cli3@bantads.com.br",
                    senhaHash,
                    "CLIENTE",
                    "85733854057"   // Catianna
            ));

            usuarioRepository.save(new Usuario(
                    null,
                    "cli4@bantads.com.br",
                    senhaHash,
                    "CLIENTE",
                    "58872160006"   // Cutardo
            ));

            usuarioRepository.save(new Usuario(
                    null,
                    "cli5@bantads.com.br",
                    senhaHash,
                    "CLIENTE",
                    "76179646090"   // Coândrya
            ));

            System.out.println(">>> [BANTADS] MongoDB populado com 9 usuários (1 admin + 3 gerentes + 5 clientes) <<<");
        }
    }
}