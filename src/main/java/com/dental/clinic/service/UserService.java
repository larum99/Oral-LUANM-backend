package com.dental.clinic.service;

import com.dental.clinic.dto.request.UserRequest;
import com.dental.clinic.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse findUserById(Long id);
    Page<UserResponse> findAllUsers(Pageable pageable);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}