package com.yahyabank.auth_users.services;

import com.yahyabank.auth_users.dtos.UpdatePasswordRequest;
import com.yahyabank.auth_users.dtos.UserDTO;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.auth_users.repo.UserRepo;
import com.yahyabank.exceptions.BadRequestException;
import com.yahyabank.exceptions.NotFoundException;
import com.yahyabank.exceptions.UserNotFoundException;
import com.yahyabank.notification.dtos.NotificationDTO;
import com.yahyabank.notification.services.NotificationService;
import com.yahyabank.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final String uploadDir = "uploads/profile-pictures";

    @Override
    public User getCurrentLoggedInUser() {

//        ===================================================================================================================================

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null) {
            throw new NotFoundException("User is not authenticated");
        }
        String email = authentication.getName();
        System.out.println(authentication.getDetails());

        return userRepo.findByEmail(email)

                .orElseThrow(() -> new UserNotFoundException("User is not found"));
    }

//    =====================================================================================================================================

    @Override
    public Response<UserDTO> getMyProfile() {

        User user = getCurrentLoggedInUser();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retrieved")
                .data(userDTO)
                .build();
    }

//    ===============================================================================================

    @Override
    public Response<Page<UserDTO>> getAllUsers(int page, int size) {

        Page<User> users = userRepo.findAll(PageRequest.of(page, size));

        Page<UserDTO> userDTOS = users.map(user ->
                modelMapper.map(user, UserDTO.class)

        );

        return Response.<Page<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retrieved")
                .data(userDTOS)
                .build();
    }


    //    =================================================================================================


    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        User user = getCurrentLoggedInUser();

        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        if (oldPassword == null || newPassword == null) {

            throw new BadRequestException("Old Password and new Password Are Required");

        } else if (!passwordEncoder.matches(oldPassword, user.getPassword())) {

            throw new BadRequestException(" Password dose note match");

        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);

        // SEND PASSWORD  NOTIFICATION EMAIL

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your Password Was Successfully Changed 🎉🎉")
                .templateName("password-change")
                .templateVariables(vars)
                .build();
        notificationService.sendEmail(notificationDTO, user);


        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password Changed Successfully")
                .build();

    }


//    =================================================================================================

    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {

        User user = getCurrentLoggedInUser();

        try {

            Path uploaPath = Paths.get(uploadDir);

            if (!Files.exists(uploaPath)) {

                Files.createDirectories(uploaPath);
            }
            if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {

                Path oldFile = Paths.get(user.getProfilePictureUrl());

                if (Files.exists(oldFile)) {

                    Files.delete(oldFile);
                }
            }

            //Generate a unique file name to avoid conflicts

            String originalFileName = file.getOriginalFilename();

            String fileExtension = "";

            if (originalFileName != null && originalFileName.contains(".")) {

                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + fileExtension;

            Path filePath = uploaPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);

            String fileUrl = uploadDir + newFileName;

            user.setProfilePictureUrl(fileUrl);
            user.setUpdatedAt(LocalDateTime.now());

            userRepo.save(user);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Profile picture uploaded successfully")
                    .data(fileUrl)
                    .build();

        } catch (IOException e) {

            throw new RuntimeException(e.getMessage());
        }

    }

//    ======================================================================================================================================


}
