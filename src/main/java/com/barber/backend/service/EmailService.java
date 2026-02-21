package com.barber.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String userName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verifica tu cuenta - Barbería");

            String verificationUrl = baseUrl + "/verify-email?token=" + token;

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .button { 
                            display: inline-block; 
                            padding: 12px 30px; 
                            background-color: #4CAF50; 
                            color: white; 
                            text-decoration: none; 
                            border-radius: 5px; 
                            margin: 20px 0;
                        }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>¡Bienvenido a la Barbería!</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <p>Gracias por registrarte en nuestro sistema de reservas.</p>
                            <p>Para completar tu registro, por favor verifica tu correo electrónico haciendo clic en el botón de abajo:</p>
                            <div style="text-align: center;">
                                <a href="%s" class="button">Verificar mi cuenta</a>
                            </div>
                            <p>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                            <p style="word-break: break-all; color: #4CAF50;">%s</p>
                            <p><strong>Este enlace expirará en 24 horas.</strong></p>
                            <p>Si no creaste esta cuenta, puedes ignorar este correo.</p>
                        </div>
                        <div class="footer">
                            <p>© 2026 Barbería - Sistema de Reservas</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, verificationUrl, verificationUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de verificación", e);
        }
    }

    public void sendAppointmentConfirmation(String toEmail, String userName, 
                                           String barbershopName, String date, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de Turno - " + barbershopName);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .appointment-details { 
                            background-color: white; 
                            padding: 15px; 
                            border-left: 4px solid #2196F3; 
                            margin: 20px 0; 
                        }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✅ Turno Confirmado</h1>
                        </div>
                        <div class="content">
                            <h2>Hola %s,</h2>
                            <p>Tu turno ha sido confirmado exitosamente.</p>
                            <div class="appointment-details">
                                <h3>Detalles del Turno:</h3>
                                <p><strong>Peluquería:</strong> %s</p>
                                <p><strong>Fecha:</strong> %s</p>
                                <p><strong>Hora:</strong> %s</p>
                            </div>
                            <p>Te esperamos! Si necesitas cancelar o modificar tu turno, puedes hacerlo desde tu cuenta.</p>
                        </div>
                        <div class="footer">
                            <p>© 2026 Barbería - Sistema de Reservas</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, barbershopName, date, time);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar confirmación de turno", e);
        }
    }
}