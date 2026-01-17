package com.yahyabank.auth_users.services;

import com.yahyabank.account.entity.Account;
import com.yahyabank.account.services.AccountService;
import com.yahyabank.auth_users.dtos.LoginRequest;
import com.yahyabank.auth_users.dtos.LoginResponse;
import com.yahyabank.auth_users.dtos.RegistrationRequest;
import com.yahyabank.auth_users.dtos.ResetPasswordRequest;
import com.yahyabank.auth_users.entity.PasswordResetCode;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.auth_users.repo.PasswordResetCodeRepo;
import com.yahyabank.auth_users.repo.UserRepo;
import com.yahyabank.enums.AccountType;
import com.yahyabank.enums.Currency;
import com.yahyabank.exceptions.BadRequestException;
import com.yahyabank.exceptions.NotFoundException;
import com.yahyabank.notification.dtos.NotificationDTO;
import com.yahyabank.notification.services.NotificationService;
import com.yahyabank.response.Response;
import com.yahyabank.role.entity.Role;
import com.yahyabank.role.repo.RoleRepo;
import com.yahyabank.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final UserRepo userRepo;

    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final NotificationService notificationService;

    private final CodeGenerator codeGenerator;

    private final PasswordResetCodeRepo passwordResetCodeRepo;

    private final AccountService accountService;

    @Value("${password.reset.link}")
    private String resetLink;



//    ====================================================================================================================================

    @Override
    public Response<String> register(RegistrationRequest request) {


        if (userRepo.findByEmail(request.getEmail()).isPresent()) {

            throw new BadRequestException("User with Email Already exists");
        }

        List<String> requestRoleName =

                //DEFAULT TO CUSTOMER

                (request.getRoles() != null && !request.getRoles().isEmpty())
                        ? request.getRoles().stream().map(String::toUpperCase).toList()
                        : List.of("CUSTOMER");

        Role deafultRole = roleRepo
                .findByName("CUSTOMER").orElseGet(() -> roleRepo.save(new Role("CUSTOMER")));


        List<Role> roles = requestRoleName.stream()
                .map(role->roleRepo.findByName(role).orElseGet(()->roleRepo.save(new Role(role))))
//                .flatMap(role1 -> role1.stream())
                .toList();



        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .isActive(true)
                .build();

        User savedUser = userRepo.save(user);

        log.info("new user registered: {} with  {} roles",savedUser.getEmail(),roles.size());

        //TODO AUTO GENERATE AN ACCOUNT NUMBER FOR THE USER

        Account savedAccount = accountService.createAccount(AccountType.SAVING, savedUser);

        //TODO SEND A WELCOME EMAIL

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", savedUser.getFirstName());
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to yahya bank 🎉🎉")
                .templateName("welcome-email")
                .templateVariables(vars)
                .build();
        notificationService.sendEmail(notificationDTO, savedUser);

        // SEND ACCOUNT DETAILS TO THE USER EMAIL

        Map<String, Object> accountVars = new HashMap<>();
        accountVars.put("name", savedUser.getFirstName());

        accountVars.put("accountNumber", savedAccount.getAccountNumber());

        accountVars.put("accountType", AccountType.SAVING.name());
        accountVars.put("currency", Currency.EGP);
        NotificationDTO accountCreationEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Your Bank Account Has Been Created ✅")
                .templateName("Account-created")
                .templateVariables(accountVars)
                .build();

        notificationService.sendEmail(accountCreationEmail, savedUser);

        return Response.<String>builder().statusCode(HttpStatus.OK.value())
                .message("Your account has been created successfully")

                .data("Email of your account details has been sent to you. Your account number is:  " + savedAccount.getAccountNumber())

                .build();
    }



    //    ====================================================================================================================================



    @Override
    public Response<LoginResponse> login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email not found "));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Password doesnt match ");
        }
        String token = tokenService.createToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles()
                        .stream()
                        .map(Role::getName).toList()).token(token).build();
        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("LogIn Successfully")
                .data(loginResponse)
                .build();
    }



    //    ====================================================================================================================================



    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found "));

        passwordResetCodeRepo.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode= PasswordResetCode.builder()
                .code(code)
                .user(user)
                .expiryDate(calculateExpiryCode())
                .used(false)
                .build();
        passwordResetCodeRepo.save(resetCode);

        //send email reset link out

        Map<String,Object> templateVariables=new HashMap<>();
        templateVariables.put("name",user.getFirstName());
        templateVariables.put("resetLink",resetLink + code);


        NotificationDTO notificationDTO= NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset1")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(notificationDTO,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code sent to your email")
                .build();
    }


    //    ====================================================================================================================================


    private LocalDateTime calculateExpiryCode() {
        return LocalDateTime.now().plusHours(5);
    }

    //    ====================================================================================================================================


    @Override
    @Transactional
    public Response<?> UpdateViaResetCodePassword(ResetPasswordRequest request) {

        String code = request.getCode();
        String newPassword = request.getNewPassword();

       //find  and validate code

        PasswordResetCode resetCode= passwordResetCodeRepo.findByCode(code)
        .orElseThrow(()-> new NotFoundException("Invalid reset code"));

       //check expiration time

        if(resetCode.getExpiryDate().isBefore(LocalDateTime.now())){
            passwordResetCodeRepo.delete(resetCode);
            throw new BadRequestException("Reset code has expired");

        }
        //update user password
        User user= resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        passwordResetCodeRepo.delete(resetCode);

        //send email reset

        Map<String,Object> templateVariables=new HashMap<>();
        templateVariables.put("name",user.getFirstName());



        NotificationDTO confirmationEmail= NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password updated Successfully")
                .templateName("password-updated-confirmation")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(confirmationEmail,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated Successfully")
                .build();
    }


    //    ====================================================================================================================================

}
