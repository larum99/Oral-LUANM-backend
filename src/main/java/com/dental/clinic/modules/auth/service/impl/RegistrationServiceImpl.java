package com.dental.clinic.modules.auth.service.impl;

import com.dental.clinic.modules.auth.dto.request.PatientRegistrationRequest;
import com.dental.clinic.modules.auth.dto.response.RegistrationResponse;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.shared.exception.DuplicateResourceException;
import com.dental.clinic.shared.exception.ResourceNotFoundException;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.auth.service.RegistrationService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(UserRepository userRepository,
                                   PatientRepository patientRepository,
                                   RoleRepository roleRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegistrationResponse registerPatient(PatientRegistrationRequest request) {
        String email = required(request.resolvedEmail(), "El correo es obligatorio.").toLowerCase(Locale.ROOT);
        String documentNumber = required(request.resolvedDocumentNumber(), "El documento es obligatorio.");
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new BusinessException("La contrasena debe tener minimo 8 caracteres.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Ya existe un usuario con ese correo.");
        }
        if (patientRepository.existsByDocumentNumber(documentNumber)) {
            throw new DuplicateResourceException("Ya existe un paciente con ese documento.");
        }
        Role patientRole = roleRepository.findByName("PACIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("Rol PACIENTE no encontrado."));
        User user = new User();
        user.setRole(patientRole);
        user.setFirstname(required(request.resolvedFirstName(), "El nombre es obligatorio."));
        user.setLastName(cleanNullable(request.resolvedLastName()));
        user.setEmail(email);
        user.setPhone(cleanNullable(request.resolvedPhone()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ACTIVO");
        user = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setDocumentType(required(request.resolvedDocumentType(), "El tipo de documento es obligatorio."));
        patient.setDocumentNumber(documentNumber);
        patient.setBirthDate(request.resolvedBirthDate());
        patient.setAcceptsData(true);
        patient.setAcceptsPromotions(false);
        patientRepository.save(patient);

        return new RegistrationResponse(user.getId(), email, "client", "Usuario registrado correctamente.");
    }

    private String required(String value, String message) {
        String clean = cleanNullable(value);
        if (clean == null) throw new BusinessException(message);
        return clean;
    }

    private String cleanNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
