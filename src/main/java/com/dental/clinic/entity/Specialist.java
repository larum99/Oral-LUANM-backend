package com.dental.clinic.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "specialists")
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_specialist")
    private Long id;

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @Column(name = "professional_license", length = 50)
    private String professionalLicense;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "specialist_services",
            joinColumns = @JoinColumn(name = "id_specialist"),
            inverseJoinColumns = @JoinColumn(name = "id_service")
    )
    private Set<DentalService> services = new HashSet<>();

    public Specialist() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getProfessionalLicense() {
        return professionalLicense;
    }

    public void setProfessionalLicense(String professionalLicense) {
        this.professionalLicense = professionalLicense;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<DentalService> getServices() {
        return services;
    }

    public void setServices(Set<DentalService> services) {
        this.services = services;
    }
}