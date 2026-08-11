package com.dental.clinic.modules.contact.service;

import com.dental.clinic.modules.contact.dto.ContactMessageRequest;
import com.dental.clinic.shared.response.MessageResponse;

public interface ContactMessageService {
    MessageResponse send(ContactMessageRequest request);
}
