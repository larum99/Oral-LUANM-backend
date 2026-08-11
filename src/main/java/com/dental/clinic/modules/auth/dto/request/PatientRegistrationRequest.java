package com.dental.clinic.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PatientRegistrationRequest {
    private String nombre;
    private String apellido;
    private String firstName;
    private String lastName;
    private String correo;
    private String email;
    private String telefono;
    private String phone;
    private LocalDate fechaDeNacimiento;
    private LocalDate birthDate;
    private String tipoDocumento;
    private String documentType;
    private String numeroDeDocumento;
    private String documentNumber;
    private Boolean acceptsTerms;
    private Boolean acceptsData;
    private Boolean acceptsPromotions;
    @NotBlank
    @Size(min = 8)
    private String password;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }
    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) { this.fechaDeNacimiento = fechaDeNacimiento; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getNumeroDeDocumento() { return numeroDeDocumento; }
    public void setNumeroDeDocumento(String numeroDeDocumento) { this.numeroDeDocumento = numeroDeDocumento; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public Boolean getAcceptsTerms() { return acceptsTerms; }
    public void setAcceptsTerms(Boolean acceptsTerms) { this.acceptsTerms = acceptsTerms; }
    public Boolean getAcceptsData() { return acceptsData; }
    public void setAcceptsData(Boolean acceptsData) { this.acceptsData = acceptsData; }
    public Boolean getAcceptsPromotions() { return acceptsPromotions; }
    public void setAcceptsPromotions(Boolean acceptsPromotions) { this.acceptsPromotions = acceptsPromotions; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Email
    public String resolvedEmail() { return pick(correo, email); }
    public String resolvedFirstName() { return pick(nombre, firstName); }
    public String resolvedLastName() { return pick(apellido, lastName); }
    public String resolvedPhone() { return pick(telefono, phone); }
    public String resolvedDocumentType() { return pick(tipoDocumento, documentType, "CC"); }
    public String resolvedDocumentNumber() { return pick(numeroDeDocumento, documentNumber); }
    public LocalDate resolvedBirthDate() { return fechaDeNacimiento != null ? fechaDeNacimiento : birthDate; }

    private String pick(String first, String second) { return pick(first, second, ""); }
    private String pick(String first, String second, String fallback) {
        String cleanFirst = first == null ? "" : first.trim();
        if (!cleanFirst.isBlank()) return cleanFirst;
        String cleanSecond = second == null ? "" : second.trim();
        return cleanSecond.isBlank() ? fallback : cleanSecond;
    }
}
