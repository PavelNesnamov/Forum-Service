package ait.cohort5860.accounting.service;

import ait.cohort5860.accounting.dto.RolesDto;
import ait.cohort5860.accounting.dto.UserDto;
import ait.cohort5860.accounting.dto.UserEditDto;
import ait.cohort5860.accounting.dto.UserRegisterDto;
import org.springframework.stereotype.Service;

@Service
public interface UserAccountService {
    UserEditDto register(UserRegisterDto userRegisterDto);

    UserDto getUser(String login);

    UserEditDto removeUser(String login);

    UserEditDto updateUser(String login, UserEditDto userEditDto);

    RolesDto changeRolesList(String login, String role, boolean isAddRole);

    void changePassword(String login, String newPassword);
}
