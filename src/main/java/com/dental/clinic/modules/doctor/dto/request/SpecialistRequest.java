package com.dental.clinic.modules.doctor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class SpecialistRequest {
    private Long userId;
    private String name;
    @Email
    private String email;
    private String phone;
    @Size(min = 8, message = "La contrasena debe tener minimo 8 caracteres.")
    private String password;
    private String specialty;
    private String professionalLicense;
    private Boolean active;
    private String status;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getProfessionalLicense() { return professionalLicense; }
    public void setProfessionalLicense(String professionalLicense) { this.professionalLicense = professionalLicense; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
