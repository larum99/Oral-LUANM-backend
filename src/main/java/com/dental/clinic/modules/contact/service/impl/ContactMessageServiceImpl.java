package com.dental.clinic.modules.contact.service.impl;

import com.dental.clinic.modules.contact.dto.ContactMessageRequest;
import com.dental.clinic.modules.contact.service.ContactMessageService;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.shared.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private static final Logger log = LoggerFactory.getLogger(ContactMessageServiceImpl.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String mailFrom;
    private final String recipient;

    public ContactMessageServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider,
                                     @Value("${app.mail.from:no-reply@oralluanm.com}") String mailFrom,
                                     @Value("${app.contact.recipient:jonathan.arevalo@genstudents.org}") String recipient) {
        this.mailSenderProvider = mailSenderProvider;
        this.mailFrom = mailFrom;
        this.recipient = recipient;
    }

    @Override
    public MessageResponse send(ContactMessageRequest request) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("SMTP no esta configurado. No se pudo enviar mensaje de contacto de {}.", request.email());
            throw new BusinessException("El envio interno de correo no esta configurado todavia.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setReplyTo(request.email());
        message.setTo(recipient);
        message.setSubject("Nuevo mensaje desde ORAL LUANM");
        message.setText(buildBody(request));

        try {
            mailSender.send(message);
            return new MessageResponse("Mensaje enviado correctamente.");
        } catch (MailException ex) {
            log.error("No fue posible enviar mensaje de contacto de {} a {}", request.email(), recipient, ex);
            throw new BusinessException("No fue posible enviar el correo en este momento.");
        }
    }

    private String buildBody(ContactMessageRequest request) {
        return "Nuevo mensaje recibido desde ORAL LUANM\n\n"
                + "Origen: " + safe(request.source()) + "\n"
                + "Nombre completo: " + safe(request.fullName()) + "\n"
                + "Telefono: " + safe(request.phone()) + "\n"
                + "Correo electronico: " + safe(request.email()) + "\n"
                + "Servicio de interes: " + safe(request.service()) + "\n\n"
                + "Mensaje:\n" + safe(request.message());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "No informado" : value.trim();
    }
}
