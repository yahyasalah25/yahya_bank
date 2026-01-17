package com.yahyabank.auth_users.entity;

import com.yahyabank.account.entity.Account;
import com.yahyabank.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {


    //===============================================================================================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Email
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    private String email;
//    @Pattern(regexp = "^[+]{1}(?:[0-9\\-\\\\.]\\s?){6,15}[0-9]{1}$")
    private String phoneNumber;

    private String password;

    private boolean emailVerified;


    private String profilePictureUrl;

    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;


    //===============================================================================================================================================

}
