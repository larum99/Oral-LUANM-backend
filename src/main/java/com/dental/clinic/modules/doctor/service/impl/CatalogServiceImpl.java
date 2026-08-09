package com.dental.clinic.modules.doctor.service.impl;

import com.dental.clinic.modules.doctor.dto.request.DentalServiceRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistRequest;
import com.dental.clinic.modules.doctor.dto.request.SpecialistScheduleRequest;
import com.dental.clinic.modules.doctor.dto.response.DentalServiceResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistScheduleResponse;
import com.dental.clinic.modules.doctor.dto.response.SpecialistServiceAssignmentResponse;
import com.dental.clinic.modules.doctor.entity.DentalService;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.doctor.entity.SpecialistSchedule;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.shared.exception.DuplicateResourceException;
import com.dental.clinic.shared.exception.ResourceNotFoundException;
import com.dental.clinic.modules.doctor.mapper.CatalogMapper;
import com.dental.clinic.modules.doctor.repository.DentalServiceRepository;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistScheduleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.doctor.service.CatalogService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private final SpecialistRepository specialistRepository;
    private final DentalServiceRepository dentalServiceRepository;
    private final SpecialistScheduleRepository specialistScheduleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CatalogMapper catalogMapper;

    public CatalogServiceImpl(SpecialistRepository specialistRepository,
                              DentalServiceRepository dentalServiceRepository,
                              SpecialistScheduleRepository specialistScheduleRepository,
                              UserRepository userRepository,
                              RoleRepository roleRepository,
                              BCryptPasswordEncoder passwordEncoder,
                              CatalogMapper catalogMapper) {
        this.specialistRepository = specialistRepository;
        this.dentalServiceRepository = dentalServiceRepository;
        this.specialistScheduleRepository = specialistScheduleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.catalogMapper = catalogMapper;
    }

    @Override
    public List<SpecialistResponse> findSpecialists() {
        return specialistRepository.findAllDetailed().stream().map(catalogMapper::toSpecialistResponse).toList();
    }

    @Override
    @Transactional
    public SpecialistResponse createSpecialist(SpecialistRequest request) {
        User user = request.getUserId() == null ? createSpecialistUser(request) : findUser(request.getUserId());
        if (specialistRepository.existsByUserId(user.getId())) {
            throw new DuplicateResourceException("El usuario ya esta asociado a un especialista.");
        }
        validateProfessionalLicense(null, request.getProfessionalLicense());
        Specialist specialist = new Specialist();
        specialist.setUser(user);
        specialist.setSpecialty(requiredText(request.getSpecialty(), "La especialidad es obligatoria."));
        specialist.setProfessionalLicense(cleanNullable(request.getProfessionalLicense()));
        specialist.setActive(resolveActive(request.getActive(), request.getStatus()));
        return catalogMapper.toSpecialistResponse(specialistRepository.save(specialist));
    }

    @Override
    @Transactional
    public SpecialistResponse updateSpecialist(Long id, SpecialistRequest request) {
        Specialist specialist = specialistRepository.findWithUserAndServicesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialista no encontrado: " + id));
        validateProfessionalLicense(specialist, request.getProfessionalLicense());
        specialist.setSpecialty(requiredText(request.getSpecialty(), "La especialidad es obligatoria."));
        specialist.setProfessionalLicense(cleanNullable(request.getProfessionalLicense()));
        specialist.setActive(resolveActive(request.getActive(), request.getStatus()));
        return catalogMapper.toSpecialistResponse(specialistRepository.save(specialist));
    }

    @Override
    public List<DentalServiceResponse> findServices() {
        return dentalServiceRepository.findAll().stream()
                .sorted(Comparator.comparing(DentalService::getName, String.CASE_INSENSITIVE_ORDER))
                .map(catalogMapper::toDentalServiceResponse)
                .toList();
    }

    @Override
    @Transactional
    public DentalServiceResponse createService(DentalServiceRequest request) {
        if (dentalServiceRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Ya existe un servicio con ese nombre.");
        }
        DentalService service = new DentalService();
        applyService(service, request);
        return catalogMapper.toDentalServiceResponse(dentalServiceRepository.save(service));
    }

    @Override
    @Transactional
    public DentalServiceResponse updateService(Long id, DentalServiceRequest request) {
        DentalService service = dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + id));
        dentalServiceRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new DuplicateResourceException("Ya existe un servicio con ese nombre."); });
        applyService(service, request);
        return catalogMapper.toDentalServiceResponse(dentalServiceRepository.save(service));
    }

    @Override
    public List<SpecialistServiceAssignmentResponse> findSpecialistServices() {
        return specialistRepository.findAllDetailed().stream()
                .flatMap(specialist -> specialist.getServices().stream()
                        .map(service -> catalogMapper.toAssignmentResponse(specialist.getId(), service)))
                .sorted(Comparator.comparing(SpecialistServiceAssignmentResponse::specialistId).thenComparing(SpecialistServiceAssignmentResponse::serviceName))
                .toList();
    }

    @Override
    @Transactional
    public void assignService(Long specialistId, Long serviceId) {
        Specialist specialist = specialistRepository.findWithUserAndServicesById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialista no encontrado: " + specialistId));
        DentalService service = dentalServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + serviceId));
        specialist.getServices().add(service);
        specialistRepository.save(specialist);
    }

    @Override
    public List<SpecialistScheduleResponse> findSchedules() {
        return specialistScheduleRepository.findAll().stream()
                .sorted(Comparator.comparing((SpecialistSchedule s) -> s.getSpecialist().getId()).thenComparing(SpecialistSchedule::getDayOfWeek).thenComparing(SpecialistSchedule::getStartTime))
                .map(catalogMapper::toScheduleResponse)
                .toList();
    }

    @Override
    @Transactional
    public SpecialistScheduleResponse createSchedule(SpecialistScheduleRequest request) {
        Specialist specialist = specialistRepository.findById(request.specialistId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialista no encontrado: " + request.specialistId()));
        if (!request.endTime().isAfter(request.startTime())) {
            throw new BusinessException("La hora final debe ser posterior a la hora inicial.");
        }
        List<SpecialistSchedule> existingSchedules = specialistScheduleRepository.findBySpecialistIdAndDayOfWeek(request.specialistId(), request.dayOfWeek());
        boolean overlap = existingSchedules.stream().anyMatch(existing ->
                request.startTime().isBefore(existing.getEndTime()) && request.endTime().isAfter(existing.getStartTime()));
        if (overlap) {
            throw new BusinessException("El horario se solapa con otro horario del especialista.");
        }
        SpecialistSchedule schedule = new SpecialistSchedule();
        schedule.setSpecialist(specialist);
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setActive(request.active() == null || request.active());
        return catalogMapper.toScheduleResponse(specialistScheduleRepository.save(schedule));
    }

    private User createSpecialistUser(SpecialistRequest request) {
        String email = requiredText(request.getEmail(), "El correo es obligatorio para crear especialista.").toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Ya existe un usuario con ese correo.");
        }
        String password = request.getPassword() == null ? "" : request.getPassword();
        if (password.length() < 8) {
            throw new BusinessException("La contrasena debe tener minimo 8 caracteres.");
        }
        Role role = roleRepository.findByName("ESPECIALISTA")
                .orElseThrow(() -> new ResourceNotFoundException("Rol ESPECIALISTA no encontrado."));
        String[] names = splitName(request.getName());
        User user = new User();
        user.setRole(role);
        user.setFirstname(names[0]);
        user.setLastName(cleanNullable(names[1]));
        user.setEmail(email);
        user.setPhone(cleanNullable(request.getPhone()));
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus("ACTIVO");
        return userRepository.save(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private void applyService(DentalService service, DentalServiceRequest request) {
        service.setName(requiredText(request.getName(), "El nombre del servicio es obligatorio."));
        service.setDescription(cleanNullable(request.getDescription()));
        service.setDurationMinutes(request.getDurationMinutes() == null ? 30 : request.getDurationMinutes());
        service.setPrice(request.getPrice());
        service.setActive(resolveActive(request.getActive(), request.getStatus()));
    }

    private void validateProfessionalLicense(Specialist current, String license) {
        String clean = cleanNullable(license);
        if (clean == null) return;
        if (current != null && clean.equals(current.getProfessionalLicense())) return;
        if (specialistRepository.existsByProfessionalLicense(clean)) {
            throw new DuplicateResourceException("Ya existe un especialista con esa licencia profesional.");
        }
    }

    private Boolean resolveActive(Boolean active, String status) {
        if (status != null && status.trim().toUpperCase(Locale.ROOT).startsWith("INACT")) return false;
        return active == null || active;
    }

    private String requiredText(String value, String message) {
        String clean = cleanNullable(value);
        if (clean == null) throw new BusinessException(message);
        return clean;
    }

    private String cleanNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String[] splitName(String name) {
        String clean = cleanNullable(name);
        if (clean == null) return new String[]{"Especialista", ""};
        String[] parts = clean.split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }
}
