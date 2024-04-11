package com.luv2code.springboot.cruddemo.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;


public class EmployeeDto {

    @NotEmpty(message = "The first name is required")
    private String firstName;

    @NotEmpty(message = "the last name is required")
    private String lastName;

    @NotEmpty(message = "The email is required")
    private String email;

    private MultipartFile image;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
