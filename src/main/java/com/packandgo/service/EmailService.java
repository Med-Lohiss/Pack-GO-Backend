package com.packandgo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            System.out.println("Correo enviado correctamente.");
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }

    public void enviarInvitacionViaje(String emailDestino, String nombreInvitador, String tituloViaje, String token) {
        String linkAceptacion = frontendUrl + "invitacion/" + token;

        String asunto = "Has sido invitado a un viaje en PackAndGo";
        String cuerpo = """
            <p>Hola,</p>
            <p>Has sido invitado por <strong>%s</strong> a unirte al viaje <strong>%s</strong>.</p>
            <p>Para aceptar la invitación, haz clic en el siguiente enlace:</p>
            <p><a href="%s">Aceptar invitación</a></p>
            <p>Si no esperabas este mensaje, puedes ignorarlo.</p>
            <p>Gracias,<br>El equipo de PackAndGo</p>
            """.formatted(nombreInvitador, tituloViaje, linkAceptacion);

        sendEmail(emailDestino, asunto, cuerpo);
    }
    
    public void notificarCreadorInvitacionAceptada(String emailCreador, String nombreInvitado, String tituloViaje) {
        String asunto = "Invitación aceptada en PackAndGo";
        String cuerpo = """
            <p>Hola,</p>
            <p>El usuario <strong>%s</strong> ha aceptado tu invitación para unirse al viaje <strong>%s</strong>.</p>
            <p>Saludos,<br>El equipo de PackAndGo</p>
            """.formatted(
                nombreInvitado != null ? nombreInvitado : "Un usuario",
                tituloViaje
            );

        sendEmail(emailCreador, asunto, cuerpo);
    }
}
