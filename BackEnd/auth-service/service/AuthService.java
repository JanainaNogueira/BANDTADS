package br.ufpr.bantads.auth_service.service;

import br.ufpr.bantads.auth_service.dto.AuthDTO;
import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Service
public class AuthService {

    private static final String SALT = "tads";

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario autenticar(AuthDTO dto) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(dto.getLogin());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();

        String senhaCriptografada = gerarSHA256(dto.getSenha(), SALT);

        if (!senhaCriptografada.equals(usuario.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        return usuario;
    }

    public String gerarSHA256(String senha, String salt) {
        try {
            String valor = senha + salt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash SHA256");
        }

    }
}