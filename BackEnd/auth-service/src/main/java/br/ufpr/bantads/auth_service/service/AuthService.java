package br.ufpr.bantads.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.auth_service.dto.AuthDTO;
import br.ufpr.bantads.auth_service.dto.AuthenticatedUserDTO;
import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    private static final String SALT = "tads";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AuthenticatedUserDTO autenticar(AuthDTO dto) {

        System.out.println(">>> LOGIN: " + dto.getLogin());
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(dto.getLogin());
        System.out.println(">>> ENCONTROU: " + usuarioOpt.isPresent());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();
        String senhaCriptografada = gerarSHA256(dto.getSenha(), SALT);
        System.out.println(">>> HASH RECEBIDO: " + senhaCriptografada);
        System.out.println(">>> HASH BANCO:    " + usuario.getSenha());

        if (!senhaCriptografada.equals(usuario.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        String token = gerarToken(usuario.getLogin(), usuario.getTipo());

        return new AuthenticatedUserDTO(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getTipo(),
                usuario.getCpf(),
                token
        );
    }

    private String gerarToken(String login, String tipo) {
        try {
            System.out.println(">>> JWT SECRET: " + jwtSecret);
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            long now = System.currentTimeMillis();
            String token = Jwts.builder()
                    .setSubject(login)
                    .claim("email", login)
                    .claim("tipo", tipo)
                    .setIssuedAt(new java.util.Date(now))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            System.out.println(">>> TOKEN OK: " + token.substring(0, 20));
            return token;
        } catch (Exception e) {
            System.out.println(">>> ERRO GERAR TOKEN: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }
    }

    public String getEmailFromToken(String jwt) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        String email = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .get("email", String.class);

        if (email != null && !email.isBlank()) {
            return email;
        }

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public String logout(String jwt) {
        return getEmailFromToken(jwt);
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
