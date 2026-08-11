package com.dental.clinic.modules.contact.controller;

import com.dental.clinic.modules.contact.dto.ContactMessageRequest;
import com.dental.clinic.modules.contact.service.ContactMessageService;
import com.dental.clinic.shared.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    public MessageResponse send(@Valid @RequestBody ContactMessageRequest request) {
        return contactMessageService.send(request);
    }
}
