package com.yahyabank.auth_users.controller;

import com.yahyabank.auth_users.dtos.*;
import com.yahyabank.auth_users.services.AuthService;
import com.yahyabank.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@PreAuthorize("hasAuthority('ADMIN')")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Response<?>> forgetPassword(@RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.forgetPassword(request.getEmail()));
    }



    @PostMapping("/reset-Password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody  ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.UpdateViaResetCodePassword(request));
    }

}
