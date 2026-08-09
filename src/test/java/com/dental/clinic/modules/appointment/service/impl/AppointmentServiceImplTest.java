package com.dental.clinic.modules.appointment.service.impl;

import com.dental.clinic.modules.appointment.dto.request.AppointmentRequest;
import com.dental.clinic.modules.appointment.dto.response.AppointmentResponse;
import com.dental.clinic.modules.doctor.entity.DentalService;
import com.dental.clinic.modules.patient.entity.Patient;
import com.dental.clinic.modules.auth.entity.Role;
import com.dental.clinic.modules.doctor.entity.Specialist;
import com.dental.clinic.modules.doctor.entity.SpecialistSchedule;
import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.shared.exception.BusinessException;
import com.dental.clinic.modules.doctor.repository.DentalServiceRepository;
import com.dental.clinic.modules.patient.repository.PatientRepository;
import com.dental.clinic.modules.auth.repository.RoleRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistRepository;
import com.dental.clinic.modules.doctor.repository.SpecialistScheduleRepository;
import com.dental.clinic.modules.auth.repository.UserRepository;
import com.dental.clinic.modules.appointment.service.AppointmentService;
import com.dental.clinic.modules.appointment.util.AppointmentRules;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AppointmentServiceImplTest {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private DentalServiceRepository dentalServiceRepository;
    @Autowired
    private SpecialistScheduleRepository specialistScheduleRepository;

    @Test
    void createsAppointmentInsideSpecialistSchedule() {
        TestFixture fixture = createFixture();
        AppointmentRequest request = request(fixture, fixture.startDateTime());

        AppointmentResponse response = appointmentService.create(request, fixture.secretary().getEmail());

        assertNotNull(response.id());
        assertEquals(fixture.patient().getId(), response.patientId());
        assertEquals(fixture.specialist().getId(), response.specialistId());
        assertEquals("PENDIENTE", response.status().name());
    }

    @Test
    void rejectsOverlappingAppointmentForSameSpecialist() {
        TestFixture fixture = createFixture();
        AppointmentRequest firstRequest = request(fixture, fixture.startDateTime());
        appointmentService.create(firstRequest, fixture.secretary().getEmail());

        AppointmentRequest overlappingRequest = request(fixture, fixture.startDateTime().plusMinutes(30));

        assertThrows(BusinessException.class,
                () -> appointmentService.create(overlappingRequest, fixture.secretary().getEmail()));
    }

    @Test
    void rejectsAppointmentOutsideSpecialistSchedule() {
        TestFixture fixture = createFixture();
        AppointmentRequest request = request(fixture, fixture.startDateTime().withHour(18));

        assertThrows(BusinessException.class,
                () -> appointmentService.create(request, fixture.secretary().getEmail()));
    }

    private AppointmentRequest request(TestFixture fixture, LocalDateTime start) {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(fixture.patient().getId());
        request.setSpecialistId(fixture.specialist().getId());
        request.setServiceId(fixture.service().getId());
        request.setStartDatetime(start.toString());
        request.setReason("Consulta de control");
        request.setNotes("Prueba automatizada");
        return request;
    }

    private TestFixture createFixture() {
        Role patientRole = role("PACIENTE");
        Role specialistRole = role("ESPECIALISTA");
        Role secretaryRole = role("SECRETARIO");

        User patientUser = user("patient-%s@test.com".formatted(System.nanoTime()), patientRole);
        Patient patient = new Patient();
        patient.setUser(patientUser);
        patient.setDocumentType("CC");
        patient.setDocumentNumber("DOC-%s".formatted(System.nanoTime()));
        patient.setAcceptsData(true);
        patient.setAcceptsPromotions(false);
        patient = patientRepository.save(patient);

        User specialistUser = user("specialist-%s@test.com".formatted(System.nanoTime()), specialistRole);
        DentalService service = new DentalService();
        service.setName("Servicio %s".formatted(System.nanoTime()));
        service.setDurationMinutes((short) 60);
        service.setActive(true);
        service = dentalServiceRepository.save(service);

        Specialist specialist = new Specialist();
        specialist.setUser(specialistUser);
        specialist.setSpecialty("Odontologia general");
        specialist.setProfessionalLicense("LIC-%s".formatted(System.nanoTime()));
        specialist.setActive(true);
        specialist.getServices().add(service);
        specialist = specialistRepository.save(specialist);

        LocalDate date = nextBookableDate();
        SpecialistSchedule schedule = new SpecialistSchedule();
        schedule.setSpecialist(specialist);
        schedule.setDayOfWeek((short) date.getDayOfWeek().getValue());
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(12, 0));
        schedule.setActive(true);
        specialistScheduleRepository.save(schedule);

        User secretary = user("secretary-%s@test.com".formatted(System.nanoTime()), secretaryRole);

        return new TestFixture(patient, specialist, service, secretary, LocalDateTime.of(date, LocalTime.of(9, 0)));
    }

    private Role role(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(name);
            return roleRepository.save(role);
        });
    }

    private User user(String email, Role role) {
        User user = new User();
        user.setRole(role);
        user.setFirstname("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhone("3001234567");
        user.setPasswordHash("hash");
        user.setStatus("ACTIVO");
        return userRepository.save(user);
    }

    private LocalDate nextBookableDate() {
        LocalDate date = LocalDate.now().plusDays(2);
        while (true) {
            LocalDateTime start = LocalDateTime.of(date, LocalTime.of(9, 0));
            try {
                AppointmentRules.validateBookableDateTime(start, start.plusMinutes(60), LocalDateTime.now());
                return date;
            } catch (IllegalArgumentException exception) {
                date = date.plusDays(1);
            }
        }
    }

    private record TestFixture(Patient patient, Specialist specialist, DentalService service, User secretary, LocalDateTime startDateTime) {
    }
}
