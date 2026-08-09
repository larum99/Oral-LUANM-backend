package com.dental.clinic.modules.appointment.entity;


import com.dental.clinic.modules.auth.entity.User;
import com.dental.clinic.modules.appointment.util.AppointmentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_history")
public class AppointmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_appointment", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private AppointmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private AppointmentStatus newStatus;

    @Column(name = "comment", length = 255)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public AppointmentStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(AppointmentStatus previousStatus) { this.previousStatus = previousStatus; }
    public AppointmentStatus getNewStatus() { return newStatus; }
    public void setNewStatus(AppointmentStatus newStatus) { this.newStatus = newStatus; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
