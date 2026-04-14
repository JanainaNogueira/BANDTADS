package br.ufpr.bantads.auth_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.ufpr.bantads.auth_service.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByLogin(String login);
}