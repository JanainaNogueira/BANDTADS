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

            // Cadastra o Administrador
            usuarioRepository.save(new Usuario(
                    null, 
                    "adm1@bantads.com.br", 
                    senhaHash, 
                    "ADMINISTRADOR", 
                    "40501740066" // CPF que o teste espera encontrar
            ));

            // Cadastro do Gerente de teste padrão
            usuarioRepository.save(new Usuario(
                    null, 
                    "ger3@bantads.com.br", 
                    senhaHash, 
                    "GERENTE", 
                    "00000000000" 
            ));

            System.out.println(">>> [BANTADS] MongoDB populado com os usuários de teste com sucesso! <<<");
        }
    }
}
