package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.DentalServiceRequest;
import com.dental.clinic.dto.response.DentalServiceResponse;
import com.dental.clinic.entity.DentalService;
import com.dental.clinic.exception.BusinessException;
import com.dental.clinic.exception.DuplicateResourceException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.DentalServiceMapper;
import com.dental.clinic.repository.DentalServiceRepository;
import com.dental.clinic.service.DentalServiceService;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class DentalServiceServiceImpl implements DentalServiceService {

    private final DentalServiceRepository dentalServiceRepository;
    private final DentalServiceMapper dentalServiceMapper;

    public DentalServiceServiceImpl(
            DentalServiceRepository dentalServiceRepository,
            DentalServiceMapper dentalServiceMapper) {

        this.dentalServiceRepository = dentalServiceRepository;
        this.dentalServiceMapper = dentalServiceMapper;
    }

    @Override
    @Transactional
    public DentalServiceResponse createDentalService(DentalServiceRequest request) {

        validateServiceCreation(request);

        DentalService dentalService = dentalServiceMapper.toEntity(request);

        DentalService savedDentalService =
                dentalServiceRepository.save(dentalService);

        return dentalServiceMapper.toResponse(savedDentalService);
    }

    @Override
    public DentalServiceResponse findDentalServiceById(Long id) {

        DentalService dentalService = findServiceEntityById(id);

        return dentalServiceMapper.toResponse(dentalService);
    }

    @Override
    public Page<DentalServiceResponse> findAllDentalServices(Pageable pageable) {

        return dentalServiceRepository.findAll(pageable)
                .map(dentalServiceMapper::toResponse);
    }

    @Override
    @Transactional
    public DentalServiceResponse updateDentalService(
            Long id,
            DentalServiceRequest request) {

        DentalService dentalService = findServiceEntityById(id);

        validateServiceUpdate(dentalService, request);

        dentalService.setName(request.name());
        dentalService.setDescription(request.description());
        dentalService.setDurationMinutes(request.durationMinutes());
        dentalService.setPrice(request.price());
        dentalService.setActive(request.active());

        DentalService updatedDentalService =
                dentalServiceRepository.save(dentalService);

        return dentalServiceMapper.toResponse(updatedDentalService);
    }

    @Override
    @Transactional
    public void deleteDentalService(Long id) {

        DentalService dentalService = findServiceEntityById(id);

        dentalServiceRepository.delete(dentalService);
    }

    private DentalService findServiceEntityById(Long id) {

        return dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SERVICE_NOT_FOUND.formatted(id)
                ));
    }

    private void validateServiceCreation(DentalServiceRequest request) {

        validateServiceNameDoesNotExist(request.name());
        validateDuration(request.durationMinutes());
        validatePrice(request.price());
    }

    private void validateServiceUpdate(
            DentalService dentalService,
            DentalServiceRequest request) {

        validateServiceNameForUpdate(
                dentalService,
                request.name()
        );

        validateDuration(request.durationMinutes());
        validatePrice(request.price());
    }

    private void validateServiceNameDoesNotExist(String name) {

        if (dentalServiceRepository.existsByName(name)) {
            throw new DuplicateResourceException(
                    MessageConstants.SERVICE_NAME_DUPLICATE.formatted(name)
            );
        }
    }

    private void validateServiceNameForUpdate(
            DentalService dentalService,
            String name) {

        if (!dentalService.getName().equals(name)) {
            validateServiceNameDoesNotExist(name);
        }
    }

    private void validateDuration(Short durationMinutes) {

        if (durationMinutes <= 0) {
            throw new BusinessException(
                    MessageConstants.INVALID_SERVICE_DURATION
            );
        }
    }

    private void validatePrice(BigDecimal price) {

        if (price != null && price.signum() < 0) {
            throw new BusinessException(
                    MessageConstants.INVALID_SERVICE_PRICE
            );
        }
    }
}