package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.UserRequest;
import com.dental.clinic.dto.response.UserResponse;
import com.dental.clinic.entity.Role;
import com.dental.clinic.entity.User;
import com.dental.clinic.exception.DuplicateResourceException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.UserMapper;
import com.dental.clinic.repository.RoleRepository;
import com.dental.clinic.repository.UserRepository;
import com.dental.clinic.service.UserService;
import com.dental.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {

        validateEmailDoesNotExist(request.email());

        Role role = findRoleById(request.roleId());


        User user = userMapper.toEntity(request, role);

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );


        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }


    @Override
    public UserResponse findUserById(Long id) {

        User user = findUserEntityById(id);

        return userMapper.toResponse(user);
    }


    @Override
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }


    @Override
    @Transactional
    public UserResponse updateUser(
            Long id,
            UserRequest request) {


        User user = findUserEntityById(id);


        validateEmailForUpdate(
                user,
                request.email()
        );


        Role role = findRoleById(request.roleId());


        user.setRole(role);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status());


        /*
         * Solo actualizamos password si viene informado.
         * Evita cambiar contraseña accidentalmente.
         */
        if (request.password() != null
                && !request.password().isBlank()) {

            user.setPasswordHash(
                    passwordEncoder.encode(request.password())
            );
        }


        User updatedUser = userRepository.save(user);


        return userMapper.toResponse(updatedUser);
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = findUserEntityById(id);

        userRepository.delete(user);
    }



    private User findUserEntityById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND.formatted(id)
                ));
    }



    private Role findRoleById(Short id) {

        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.ROLE_NOT_FOUND.formatted(id)
                ));
    }



    private void validateEmailDoesNotExist(String email) {

        if (userRepository.existsByEmail(email)) {

            throw new DuplicateResourceException(
                    MessageConstants.EMAIL_ALREADY_EXISTS.formatted(email)
            );
        }
    }



    private void validateEmailForUpdate(
            User user,
            String email) {


        if (!user.getEmail().equals(email)) {

            validateEmailDoesNotExist(email);
        }
    }
}