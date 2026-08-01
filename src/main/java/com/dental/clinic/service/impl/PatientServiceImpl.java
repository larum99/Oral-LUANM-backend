package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.PatientRequest;
import com.dental.clinic.dto.response.PatientResponse;
import com.dental.clinic.entity.Patient;
import com.dental.clinic.entity.User;
import com.dental.clinic.exception.DuplicateResourceException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.PatientMapper;
import com.dental.clinic.repository.PatientRepository;
import com.dental.clinic.repository.UserRepository;
import com.dental.clinic.service.PatientService;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientMapper patientMapper) {

        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {

        validateDocumentNumberDoesNotExist(
                request.documentNumber()
        );

        validateUserIsNotAssociatedWithPatient(
                request.userId()
        );

        User user = findUserById(request.userId());

        Patient patient = patientMapper.toEntity(request, user);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }

    @Override
    public PatientResponse findPatientById(Long id) {
        Patient patient = findPatientEntityById(id);
        return patientMapper.toResponse(patient);
    }

    @Override
    public Page<PatientResponse> findAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(
            Long id,
            PatientRequest request) {

        Patient patient = findPatientEntityById(id);

        validateDocumentNumberForUpdate(
                patient,
                request.documentNumber()
        );

        validateUserForUpdate(
                id,
                request.userId()
        );

        User user = findUserById(request.userId());

        patient.setUser(user);
        patient.setDocumentType(request.documentType());
        patient.setDocumentNumber(request.documentNumber());
        patient.setBirthDate(request.birthDate());
        patient.setAcceptsData(request.acceptsData());
        patient.setAcceptsPromotions(request.acceptsPromotions());

        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponse(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = findPatientEntityById(id);
        patientRepository.delete(patient);
    }

    private Patient findPatientEntityById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PATIENT_NOT_FOUND.formatted(id)
                ));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND.formatted(id)
                ));
    }

    private void validateDocumentNumberDoesNotExist(String documentNumber) {
        if (patientRepository.existsByDocumentNumber(documentNumber)) {
            throw new DuplicateResourceException(
                    MessageConstants.PATIENT_DOCUMENT_DUPLICATE.formatted(documentNumber)
            );
        }
    }

    private void validateDocumentNumberForUpdate(
            Patient patient,
            String documentNumber) {

        if (!patient.getDocumentNumber().equals(documentNumber)) {
            validateDocumentNumberDoesNotExist(documentNumber);
        }
    }

    private void validateUserIsNotAssociatedWithPatient(Long userId) {
        if (patientRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException(
                    MessageConstants.USER_ALREADY_ASSOCIATED.formatted(userId)
            );
        }
    }

    private void validateUserForUpdate(
            Long patientId,
            Long userId) {

        patientRepository.findByUserId(userId)
                .ifPresent(patient -> {
                    if (!patient.getId().equals(patientId)) {
                        throw new DuplicateResourceException(
                                MessageConstants.USER_ALREADY_ASSOCIATED_OTHER.formatted(userId)
                        );
                    }
                });
    }
}
