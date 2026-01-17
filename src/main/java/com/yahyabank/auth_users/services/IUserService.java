package com.yahyabank.auth_users.services;

import com.yahyabank.auth_users.dtos.UpdatePasswordRequest;
import com.yahyabank.auth_users.dtos.UserDTO;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.response.Response;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    User getCurrentLoggedInUser();

    Response<UserDTO> getMyProfile();

    Response<Page<UserDTO>> getAllUsers(int page,int size);

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    Response<?> uploadProfilePicture(MultipartFile file);


}
