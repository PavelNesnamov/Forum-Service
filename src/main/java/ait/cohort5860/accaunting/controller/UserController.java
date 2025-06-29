package ait.cohort5860.accaunting.controller;


import ait.cohort5860.accaunting.dto.*;
import ait.cohort5860.accaunting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getLogin(), loginDto.getPassword());
    }

    @GetMapping("/login/{login}")
    public UserResponseDto getUserByLogin(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }

    @PutMapping("/user/{login}")
    public UserResponseDto updateUser(@PathVariable String login, @RequestBody UserUpdateDto updateDto) {
        return userService.updateUser(login, updateDto);
    }

    @DeleteMapping("/user/{login}")
    public void deleteUser(@PathVariable String login) {
        userService.deleteUser(login);
    }

    @PutMapping("/password/{login}")
    public void changePassword(@PathVariable String login, @RequestBody ChangePasswordDto dto) {
        userService.changePassword(login, dto.getOldPassword(), dto.getNewPassword());
    }

    @PutMapping("/role/{login}")
    public UserResponseDto addRole(@PathVariable String login, @RequestBody RoleDto role) {
        return userService.addRole(login, role.getRole());
    }

    @DeleteMapping("/role/{login}/{role}")
    public UserResponseDto removeRole(@PathVariable String login, @PathVariable String role) {
        return userService.removeRole(login, role);
    }
}
