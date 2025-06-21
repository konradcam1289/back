package com.workshopapp.workshopservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ROLE_ADMIN,
    ROLE_CLIENT,
    ROLE_WORKER;

    @JsonValue
    public String getShortName() {
        return name().replace("ROLE_", "");
    }

    @JsonCreator
    public static Role fromShortName(String shortName) {
        return Role.valueOf("ROLE_" + shortName);
    }
}
