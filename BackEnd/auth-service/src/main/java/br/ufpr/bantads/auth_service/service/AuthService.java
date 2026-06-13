package br.ufpr.bantads.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private static final String FILA_MS = "fila-auth";
    private static final String FILA_SAGA = "fila-saga";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public AuthenticatedUserDTO autenticar(AuthDTO dto) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(dto.getLogin());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();
        String senhaCriptografada = gerarSHA256(dto.getSenha(), SALT);

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

    @RabbitListener(queues = FILA_MS)
    public void consumirMensagensSaga(Map<String, Object> mensagem) {
        String acao = mensagem.get("acao") != null ? mensagem.get("acao").toString() : null;

        if ("CRIAR_AUTH".equals(acao) || "CRIAR_AUTH_CLIENTE".equals(acao)) {
            processarCriacaoAuth(mensagem);
        }
    }

    private void processarCriacaoAuth(Map<String, Object> mensagem) {
        String idSaga = mensagem.get("idSaga") != null ? mensagem.get("idSaga").toString() : null;

        try {
            Map<String, Object> credenciais = extrairCredenciais(mensagem);
            String cpf = credenciais.get("cpf").toString();
            String email = credenciais.get("email").toString();
            String senha = credenciais.get("senha").toString();

            criarCredencialCliente(cpf, email, senha);

            if (idSaga != null) {
                responderSaga(idSaga, "AUTH_CRIADO_SUCESSO", cpf);
            }
        } catch (Exception e) {
            if (idSaga != null) {
                responderSaga(idSaga, "AUTH_CRIADO_ERRO", e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extrairCredenciais(Map<String, Object> mensagem) {
        if (mensagem.containsKey("dados") && mensagem.get("dados") != null) {
            return objectMapper.convertValue(mensagem.get("dados"), Map.class);
        }

        return mensagem;
    }

    public void criarCredencialCliente(String cpf, String email, String senhaPlana) {
        String senhaHash = gerarSHA256(senhaPlana, SALT);

        Optional<Usuario> existente = usuarioRepository.findByLogin(email);

        if (existente.isPresent()) {
            Usuario usuario = existente.get();
            usuario.setSenha(senhaHash);
            usuario.setCpf(cpf);
            usuario.setTipo("CLIENTE");
            usuarioRepository.save(usuario);
            return;
        }

        usuarioRepository.save(new Usuario(null, email, senhaHash, "CLIENTE", cpf));
    }

    private void responderSaga(String idSaga, String acao, Object dados) {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("idSaga", idSaga);
        resposta.put("acao", acao);
        resposta.put("dados", dados);

        rabbitTemplate.convertAndSend(FILA_SAGA, resposta);
    }

    private String gerarToken(String login, String tipo) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(login)
                .claim("email", login)
                .claim("tipo", tipo)
                .setIssuedAt(new java.util.Date(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
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
