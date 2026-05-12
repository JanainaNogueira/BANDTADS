package br.ufpr.bantads.cliente_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.cliente_service.service.EmailService;

@RestController
@RequestMapping("/test-email")
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String testEmail(@RequestParam String to) {
        emailService.enviarEmail(to, "Teste BANTADS", "Este é um e-mail de teste do sistema BANTADS.");
        return "Comando de envio enviado para: " + to + ". Verifique os logs do servidor.";
    }
}
