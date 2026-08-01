package com.dental.clinic.utils;

public final class MessageConstants {

    private MessageConstants() {
    }

    public static final String PATIENT_NOT_FOUND = "Patient with id %s was not found";
    public static final String USER_NOT_FOUND = "User with id %s was not found";
    public static final String PATIENT_DOCUMENT_DUPLICATE = "Patient with document number %s already exists";
    public static final String USER_ALREADY_ASSOCIATED = "User with id %s is already associated with a patient";
    public static final String USER_ALREADY_ASSOCIATED_OTHER = "User with id %s is already associated with another patient";

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
