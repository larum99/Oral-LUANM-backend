package com.dental.clinic.modules.doctor.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class DentalServiceRequest {
    @NotBlank
    private String name;
    private String description;
    @Min(1)
    private Short durationMinutes;
    private BigDecimal price;
    private Boolean active;
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Short getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Short durationMinutes) { this.durationMinutes = durationMinutes; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
