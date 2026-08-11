package com.dental.clinic.modules.appointment.service.impl;

import com.dental.clinic.modules.appointment.dto.request.AppointmentRequest;
import com.dental.clinic.modules.appointment.dto.request.AppointmentStatusRequest;
import com.dental.clinic.modules.appointment.dto.response.AppointmentResponse;
import com.dental.clinic.modules.appointment.entity.Appointment;
import com.dental.clinic.modules.appointment.entity.AppointmentHistory;
import com.dental.clinic.modules.doctor.entity.DentalService;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.doctor.entity.SpecialistSchedule;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.shared.exception.ResourceNotFoundException;
import com.dental.clinic.modules.appointment.mapper.AppointmentMapper;
import com.dental.clinic.modules.appointment.repository.AppointmentHistoryRepository;
import com.dental.clinic.modules.appointment.repository.AppointmentRepository;
import com.dental.clinic.modules.doctor.repository.DentalServiceRepository;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistScheduleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.appointment.service.AppointmentService;
import com.dental.clinic.modules.appointment.util.AppointmentRules;
import com.dental.clinic.modules.appointment.util.AppointmentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private static final String ACTIVE_STATUS = "ACTIVO";

    private final AppointmentRepository appointmentRepository;
    private final AppointmentHistoryRepository appointmentHistoryRepository;
    private final PatientRepository patientRepository;
    private final SpecialistRepository specialistRepository;
    private final DentalServiceRepository dentalServiceRepository;
    private final SpecialistScheduleRepository specialistScheduleRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentHistoryRepository appointmentHistoryRepository,
            PatientRepository patientRepository,
            SpecialistRepository specialistRepository,
            DentalServiceRepository dentalServiceRepository,
            SpecialistScheduleRepository specialistScheduleRepository,
            UserRepository userRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentHistoryRepository = appointmentHistoryRepository;
        this.patientRepository = patientRepository;
        this.specialistRepository = specialistRepository;
        this.dentalServiceRepository = dentalServiceRepository;
        this.specialistScheduleRepository = specialistScheduleRepository;
        this.userRepository = userRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAllDetailed().stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> findMine(String userEmail) {
        User user = findUserByEmail(userEmail);
        return appointmentRepository.findDetailedByPatientUserId(user.getId()).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentRequest request, String createdByEmail) {
        User createdBy = findOptionalUserByEmail(createdByEmail);
        Appointment appointment = buildAppointment(request, request.getPatientId(), createdBy, AppointmentStatus.PENDIENTE, null);
        Appointment saved = appointmentRepository.save(appointment);
        insertHistory(saved, null, saved.getStatus(), "Cita creada", createdBy);
        return appointmentMapper.toResponse(appointmentRepository.findDetailedById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public AppointmentResponse createMine(AppointmentRequest request, String patientUserEmail) {
        User currentUser = findUserByEmail(patientUserEmail);
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("El usuario autenticado no tiene paciente asociado."));
        Appointment appointment = buildAppointment(request, patient.getId(), currentUser, AppointmentStatus.PENDIENTE, null);
        Appointment saved = appointmentRepository.save(appointment);
        insertHistory(saved, null, saved.getStatus(), "Cita creada por paciente", currentUser);
        return appointmentMapper.toResponse(appointmentRepository.findDetailedById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request, String changedByEmail) {
        Appointment appointment = appointmentRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada: " + id));
        User changedBy = findOptionalUserByEmail(changedByEmail);
        if (isPatientUser(changedBy) && !isAppointmentOwner(appointment, changedBy)) {
            throw new BusinessException("No puedes modificar una cita de otro paciente.");
        }
        AppointmentStatus previousStatus = appointment.getStatus();
        AppointmentStatus requestedStatus = request.resolvedStatus();
        Long patientId = request.getPatientId() != null ? request.getPatientId() : appointment.getPatient().getId();
        applyAppointmentData(appointment, request, patientId, requestedStatus, id);
        Appointment saved = appointmentRepository.save(appointment);
        if (previousStatus != requestedStatus) {
            insertHistory(saved, previousStatus, requestedStatus, "Estado actualizado", changedBy);
        }
        return appointmentMapper.toResponse(appointmentRepository.findDetailedById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatusRequest request, String changedByEmail) {
        Appointment appointment = appointmentRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada: " + id));
        User changedBy = findOptionalUserByEmail(changedByEmail);
        if (isPatientUser(changedBy) && !isAppointmentOwner(appointment, changedBy)) {
            throw new BusinessException("No puedes modificar una cita de otro paciente.");
        }
        AppointmentStatus previousStatus = appointment.getStatus();
        AppointmentStatus newStatus = parseStatus(request.status());
        appointment.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        insertHistory(saved, previousStatus, newStatus, request.comment() == null || request.comment().isBlank() ? "Estado actualizado" : request.comment(), changedBy);
        return appointmentMapper.toResponse(appointmentRepository.findDetailedById(saved.getId()).orElseThrow());
    }

    private boolean isPatientUser(User user) {
        return user != null
                && user.getRole() != null
                && "PACIENTE".equalsIgnoreCase(user.getRole().getName());
    }

    private boolean isAppointmentOwner(Appointment appointment, User user) {
        return appointment.getPatient() != null
                && appointment.getPatient().getUser() != null
                && appointment.getPatient().getUser().getId().equals(user.getId());
    }
    private Appointment buildAppointment(AppointmentRequest request, Long patientId, User createdBy, AppointmentStatus status, Long excludeId) {
        Appointment appointment = new Appointment();
        appointment.setCreatedBy(createdBy);
        applyAppointmentData(appointment, request, patientId, status, excludeId);
        return appointment;
    }

    private void applyAppointmentData(Appointment appointment, AppointmentRequest request, Long patientId, AppointmentStatus status, Long excludeId) {
        if (patientId == null) {
            throw new BusinessException("El paciente es obligatorio para crear o actualizar una cita.");
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado: " + patientId));
        Specialist specialist = specialistRepository.findWithUserAndServicesById(request.getSpecialistId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialista no encontrado: " + request.getSpecialistId()));
        DentalService service = request.getServiceId() == null
                ? null
                : dentalServiceRepository.findById(request.getServiceId())
                        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + request.getServiceId()));

        LocalDateTime start = request.resolvedStartDatetime();
        int durationMinutes = service == null || service.getDurationMinutes() == null ? 30 : service.getDurationMinutes();
        LocalDateTime end = start.plusMinutes(durationMinutes);

        validateBookableAppointment(patient, specialist, service, start, end, excludeId);

        appointment.setPatient(patient);
        appointment.setSpecialist(specialist);
        appointment.setService(service);
        appointment.setStartDatetime(start);
        appointment.setEndDatetime(end);
        appointment.setReason(request.getReason().trim());
        appointment.setNotes(cleanNullable(request.getNotes()));
        appointment.setStatus(status);
    }

    private void validateBookableAppointment(Patient patient, Specialist specialist, DentalService service, LocalDateTime start, LocalDateTime end, Long excludeId) {
        AppointmentRules.validateBookableDateTime(start, end, LocalDateTime.now());
        if (patient.getUser() != null && !ACTIVE_STATUS.equals(patient.getUser().getStatus())) {
            throw new BusinessException("El paciente no esta activo.");
        }
        if (!Boolean.TRUE.equals(specialist.getActive()) || specialist.getUser() == null || !ACTIVE_STATUS.equals(specialist.getUser().getStatus())) {
            throw new BusinessException("El especialista no esta activo.");
        }
        if (service != null && !Boolean.TRUE.equals(service.getActive())) {
            throw new BusinessException("El servicio no esta activo.");
        }
        if (service != null && specialist.getServices().stream().noneMatch(item -> item.getId().equals(service.getId()))) {
            throw new BusinessException("El especialista no presta el servicio seleccionado.");
        }
        ensureSpecialistSchedule(specialist.getId(), start, end);
        if (appointmentRepository.existsOverlappingForSpecialist(specialist.getId(), start, end, AppointmentStatus.CANCELADA, excludeId)) {
            throw new BusinessException("Ya existe una cita para ese especialista en ese rango horario.");
        }
        if (appointmentRepository.existsOverlappingForPatient(patient.getId(), start, end, AppointmentStatus.CANCELADA, excludeId)) {
            throw new BusinessException("El paciente ya tiene una cita en ese rango horario.");
        }
    }

    private void ensureSpecialistSchedule(Long specialistId, LocalDateTime start, LocalDateTime end) {
        Short dayOfWeek = (short) start.toLocalDate().getDayOfWeek().getValue();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();
        List<SpecialistSchedule> schedules = specialistScheduleRepository.findBySpecialistIdAndDayOfWeekAndActiveTrue(specialistId, dayOfWeek);
        boolean available = schedules.stream()
                .anyMatch(schedule -> !schedule.getStartTime().isAfter(startTime) && !schedule.getEndTime().isBefore(endTime));
        if (!available) {
            throw new BusinessException("El especialista no tiene disponibilidad en ese horario.");
        }
    }

    private void insertHistory(Appointment appointment, AppointmentStatus previousStatus, AppointmentStatus newStatus, String comment, User changedBy) {
        AppointmentHistory history = new AppointmentHistory();
        history.setAppointment(appointment);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setComment(cleanNullable(comment));
        history.setChangedBy(changedBy);
        appointmentHistoryRepository.save(history);
    }

    private User findUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResourceNotFoundException("Usuario autenticado no encontrado.");
        }
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }

    private User findOptionalUserByEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    private AppointmentStatus parseStatus(String status) {
        AppointmentRequest request = new AppointmentRequest();
        request.setStatus(status);
        return request.resolvedStatus();
    }

    private String cleanNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
