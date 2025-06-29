package ait.cohort5860.accaunting.service;

import ait.cohort5860.accaunting.dto.UserRegisterDto;
import ait.cohort5860.accaunting.dto.UserResponseDto;
import ait.cohort5860.accaunting.dto.UserUpdateDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDto register(UserRegisterDto userRegisterDto);

    UserResponseDto login(String login, String password);

    UserResponseDto getUserByLogin(String login);

    UserResponseDto updateUser(String login, UserUpdateDto updateDto);

    void deleteUser(String login);

    void changePassword(String login, String oldPassword, String newPassword);

    UserResponseDto addRole(String login, String role);

    UserResponseDto removeRole(String login, String role);

    UserResponseDto addNewUser(String login, UserRegisterDto userRegisterDto);

    UserResponseDto getLogin(UserUpdateDto updateDto);
}
