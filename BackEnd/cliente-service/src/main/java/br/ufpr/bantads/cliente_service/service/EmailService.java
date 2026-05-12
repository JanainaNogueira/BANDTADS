package br.ufpr.bantads.cliente_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void enviarEmail(String to, String subject, String text) {
        if (mailSender == null) {
            System.out.println("JavaMailSender não configurado. Simulando envio de e-mail para: " + to);
            System.out.println("Assunto: " + subject);
            System.out.println("Corpo: " + text);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@bantads.com.br");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso para: " + to);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail para: " + to);
            e.printStackTrace();
        }
    }
}