package com.yahyabank.auth_users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yahyabank.account.entity.Account;
import com.yahyabank.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;

    private String firstName;

    private String lastName;


    private String email;

    private String phoneNumber;

    @JsonIgnore
    private String password;

    private boolean emailVerified;


    private String profilePictureUrl;

    private boolean active;


    private List<Role> roles;

    @JsonManagedReference
    private List<Account> accounts;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
