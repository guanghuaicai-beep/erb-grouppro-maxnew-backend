package com.nick.myApp.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    VISA("VISA"),
    MASTER("MASTER"),
    ALIPAY("ALIPAY"),
    PAYME("PAYME"),
    NA("NA");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NA;
        }
        String upper = value.trim().toUpperCase();
        for (PaymentMethod pm : values()) {
            if (pm.value.equalsIgnoreCase(upper)) {
                return pm;
            }
        }
        return NA;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
