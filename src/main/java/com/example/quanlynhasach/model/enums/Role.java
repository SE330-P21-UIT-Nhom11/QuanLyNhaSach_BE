package com.example.quanlynhasach.model.enums;

public enum Role {
    USER("USER"),
    EMPLOYEE("EMPLOYEE"),
    ADMIN("ADMIN");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
