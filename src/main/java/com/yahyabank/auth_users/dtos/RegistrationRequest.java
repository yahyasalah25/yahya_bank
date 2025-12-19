package com.yahyabank.auth_users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationRequest {


    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    private List<String> roles;

    @NotBlank(message = "password is required")
    private String password;
}
