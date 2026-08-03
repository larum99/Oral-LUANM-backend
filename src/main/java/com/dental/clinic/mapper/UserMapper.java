package com.dental.clinic.mapper;

import com.dental.clinic.dto.request.UserRequest;
import com.dental.clinic.dto.response.UserResponse;
import com.dental.clinic.entity.Role;
import com.dental.clinic.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public User toEntity(UserRequest request, Role role) {

        User user = new User();

        user.setRole(role);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status());

        return user;
    }


    public UserResponse toResponse(User user) {

        Long roleId = user.getRole() != null
                ? user.getRole().getId().longValue()
                : null;

        String roleName = user.getRole() != null
                ? user.getRole().getName()
                : null;


        return new UserResponse(
                user.getId(),
                roleId,
                roleName,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin()
        );
    }
}