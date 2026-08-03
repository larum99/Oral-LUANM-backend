package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.SpecialistRequest;
import com.dental.clinic.dto.response.SpecialistResponse;
import com.dental.clinic.entity.DentalService;
import com.dental.clinic.entity.Specialist;
import com.dental.clinic.entity.User;
import com.dental.clinic.exception.DuplicateResourceException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.SpecialistMapper;
import com.dental.clinic.repository.DentalServiceRepository;
import com.dental.clinic.repository.SpecialistRepository;
import com.dental.clinic.repository.UserRepository;
import com.dental.clinic.service.SpecialistService;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final DentalServiceRepository dentalServiceRepository;
    private final SpecialistMapper specialistMapper;

    public SpecialistServiceImpl(
            SpecialistRepository specialistRepository,
            UserRepository userRepository,
            DentalServiceRepository dentalServiceRepository,
            SpecialistMapper specialistMapper) {

        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.dentalServiceRepository = dentalServiceRepository;
        this.specialistMapper = specialistMapper;
    }

    @Override
    @Transactional
    public SpecialistResponse createSpecialist(SpecialistRequest request) {
        validateProfessionalLicenseDoesNotExist(
                request.professionalLicense()
        );

        validateUserIsNotAssociatedWithSpecialist(
                request.userId()
        );

        User user = findUserById(request.userId());

        Set<DentalService> services = findServicesByIds(request.serviceIds());

        Specialist specialist = specialistMapper.toEntity(request, user, services);
        Specialist savedSpecialist = specialistRepository.save(specialist);

        return specialistMapper.toResponse(savedSpecialist);
    }

    @Override
    public SpecialistResponse findSpecialistById(Long id) {
        Specialist specialist = findSpecialistEntityById(id);
        return specialistMapper.toResponse(specialist);
    }

    @Override
    public Page<SpecialistResponse> findAllSpecialists(Pageable pageable) {
        return specialistRepository.findAll(pageable)
                .map(specialistMapper::toResponse);
    }

    @Override
    @Transactional
    public SpecialistResponse updateSpecialist(
            Long id,
            SpecialistRequest request) {

        Specialist specialist = findSpecialistEntityById(id);

        validateProfessionalLicenseForUpdate(
                specialist,
                request.professionalLicense()
        );

        validateUserForUpdate(
                id,
                request.userId()
        );

        User user = findUserById(request.userId());

        Set<DentalService> services = findServicesByIds(request.serviceIds());

        specialist.setUser(user);
        specialist.setSpecialty(request.specialty());
        specialist.setProfessionalLicense(request.professionalLicense());
        specialist.setActive(request.active());
        specialist.setServices(services);

        Specialist updatedSpecialist =
                specialistRepository.save(specialist);

        return specialistMapper.toResponse(updatedSpecialist);
    }

    @Override
    @Transactional
    public void deleteSpecialist(Long id) {
        Specialist specialist = findSpecialistEntityById(id);
        specialistRepository.delete(specialist);
    }

    private Specialist findSpecialistEntityById(Long id) {

        return specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SPECIALIST_NOT_FOUND.formatted(id)
                ));
    }

    private User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND.formatted(id)
                ));
    }

    private Set<DentalService> findServicesByIds(Set<Long> serviceIds) {
        Set<DentalService> services = new HashSet<>(dentalServiceRepository.findAllById(serviceIds));
        if(services.size() != serviceIds.size()) {
            throw new ResourceNotFoundException(MessageConstants.SERVICES_NOT_FOUND);
        }

        return services;
    }

    private void validateProfessionalLicenseDoesNotExist(String license) {

        if (license != null &&
                specialistRepository.existsByProfessionalLicense(license)) {

            throw new DuplicateResourceException(
                    MessageConstants.SPECIALIST_LICENSE_DUPLICATE.formatted(license)
            );
        }
    }

    private void validateProfessionalLicenseForUpdate(
            Specialist specialist,
            String license) {

        if (!java.util.Objects.equals(
                specialist.getProfessionalLicense(),
                license)) {

            validateProfessionalLicenseDoesNotExist(license);
        }
    }

    private void validateUserIsNotAssociatedWithSpecialist(Long userId) {

        if (specialistRepository.existsByUserId(userId)) {

            throw new DuplicateResourceException(
                    MessageConstants.USER_ALREADY_ASSOCIATED_SPECIALIST.formatted(userId)
            );
        }
    }

    private void validateUserForUpdate(
            Long specialistId,
            Long userId) {

        specialistRepository.findByUserId(userId)
                .ifPresent(specialist -> {
                    if (!specialist.getId().equals(specialistId)) {
                        throw new DuplicateResourceException(
                                MessageConstants.USER_ALREADY_ASSOCIATED_OTHER_SPECIALIST.formatted(userId)
                        );
                    }
                });
    }
}