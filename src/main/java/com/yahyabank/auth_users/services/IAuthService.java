package com.yahyabank.auth_users.services;

import com.yahyabank.auth_users.dtos.LoginRequest;
import com.yahyabank.auth_users.dtos.LoginResponse;
import com.yahyabank.auth_users.dtos.RegistrationRequest;
import com.yahyabank.auth_users.dtos.ResetPasswordRequest;
import com.yahyabank.response.Response;

public interface IAuthService {

    Response<String> register(RegistrationRequest request);
    Response<LoginResponse> login(LoginRequest request);
    Response<?> forgetPassword(String email);
    Response<?> UpdateViaResetCodePassword(ResetPasswordRequest request);
//    Response<String> register(Re request);
}
