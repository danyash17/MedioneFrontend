package bsu.rpact.medionefrontend.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    @JsonValue
    PATIENT,
    @JsonValue
    DOCTOR,
    @JsonValue
    ADMIN
}
