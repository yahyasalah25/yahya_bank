package com.yahyabank.role.services;

import com.yahyabank.exceptions.BadRequestException;
import com.yahyabank.exceptions.NotFoundException;
import com.yahyabank.response.Response;
import com.yahyabank.role.entity.Role;
import com.yahyabank.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    @Override
    public Response<Role> createRole(Role roleRequest) {
        if (roleRepo.findByName(roleRequest.getName()).isPresent()) {
            throw new BadRequestException("Role Already exists");

        }
        Role savedRole = roleRepo.save(roleRequest);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(savedRole)
                .build();
    }

    @Override
    public Response<Role> updateRole(Role roleRequest) {
        Role role = roleRepo.findById(roleRequest.getId())
                .orElseThrow(() -> new NotFoundException("Role not found "));
        role.setName(roleRequest.getName());
        Role savedRole = roleRepo.save(role);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(savedRole).build();
    }

    @Override
    public Response<List<Role>> getAllRole() {

        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(roleRepo.findAll())
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {

            throw new NotFoundException("role  not found ");
        }
        roleRepo.deleteById(id);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .build();
    }

}
