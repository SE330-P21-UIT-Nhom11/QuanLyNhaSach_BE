package com.example.quanlynhasach.dto;

public class AuthorDTO {
    private int id;
    private String name;
    private String bio;

    public AuthorDTO() {
    }

    public AuthorDTO(int id, String name, String bio) {
        this.id = id;
        this.name = name;
        this.bio = bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}