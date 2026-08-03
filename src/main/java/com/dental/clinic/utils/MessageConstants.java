package com.dental.clinic.utils;

public final class MessageConstants {

    private MessageConstants() {
    }

    public static final String PATIENT_NOT_FOUND = "Patient with id %s was not found";
    public static final String USER_NOT_FOUND = "User with id %s was not found";
    public static final String PATIENT_DOCUMENT_DUPLICATE = "Patient with document number %s already exists";
    public static final String USER_ALREADY_ASSOCIATED = "User with id %s is already associated with a patient";
    public static final String USER_ALREADY_ASSOCIATED_OTHER = "User with id %s is already associated with another patient";
    public static final String SERVICE_NOT_FOUND = "Service with id %s was not found";
    public static final String SERVICES_NOT_FOUND= "One or more services were not found";
    public static final String SERVICE_NAME_DUPLICATE = "Service with name '%s' already exists";
    public static final String INVALID_SERVICE_DURATION = "Service duration must be greater than zero";
    public static final String INVALID_SERVICE_PRICE = "Service price cannot be negative";
    public static final String SPECIALIST_NOT_FOUND = "Specialist with id %s was not found";
    public static final String SPECIALIST_LICENSE_DUPLICATE = "Specialist with professional license %s already exists";
    public static final String USER_ALREADY_ASSOCIATED_SPECIALIST = "User with id %s is already associated with a specialist";
    public static final String USER_ALREADY_ASSOCIATED_OTHER_SPECIALIST = "User with id %s is already associated with another specialist";
    public static final String SCHEDULE_NOT_FOUND = "Schedule with id %d was not found.";
    public static final String INVALID_SCHEDULE_TIME_RANGE = "End time must be after start time.";
    public static final String SCHEDULE_OVERLAP = "The specialist already has a schedule that overlaps this time range.";
    public static final String EMAIL_ALREADY_EXISTS = "User with email %s already exists";
    public static final String ROLE_NOT_FOUND = "Role with id %s was not found";

    public static final String ERROR_TITLE_RESOURCE_NOT_FOUND = "Resource Not Found";
    public static final String ERROR_TITLE_DUPLICATE_RESOURCE = "Duplicate Resource";
    public static final String ERROR_TITLE_BUSINESS_ERROR = "Business Error";
    public static final String ERROR_TITLE_VALIDATION_FAILED = "Validation Failed";
    public static final String ERROR_TITLE_MALFORMED_REQUEST = "Malformed Request";
    public static final String ERROR_TITLE_DATA_CONFLICT = "Data Conflict";

    public static final String ERROR_MESSAGE_MALFORMED_REQUEST = "Request body is missing or contains invalid values";
    public static final String ERROR_MESSAGE_DATA_CONFLICT = "The request conflicts with existing data (duplicate or referenced record)";
    public static final String ERROR_MESSAGE_INTERNAL = "An unexpected error occurred.";
}
