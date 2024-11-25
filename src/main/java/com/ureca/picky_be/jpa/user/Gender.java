package com.ureca.picky_be.jpa.user;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String value;

    Gender(String value) {
        this.value = value;
    }
}