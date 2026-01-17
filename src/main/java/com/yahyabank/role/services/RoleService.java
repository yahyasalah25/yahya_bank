package com.yahyabank.role.services;

import com.yahyabank.response.Response;
import com.yahyabank.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);

    Response<Role> updateRole(Role roleRequest);

    Response<List<Role>> getAllRole();

    Response<?> deleteRole(Long id);
}
