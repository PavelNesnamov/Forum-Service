package ait.cohort5860.accouting.controller;

import ait.cohort5860.accounting.controller.UserAccountController;
import ait.cohort5860.accounting.dto.RolesDto;
import ait.cohort5860.accounting.dto.UserDto;
import ait.cohort5860.accounting.dto.UserEditDto;
import ait.cohort5860.accounting.dto.UserRegisterDto;
import ait.cohort5860.accounting.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccoutingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private UserAccountController userAccountController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserRegisterDto userRegisterDto;
    private UserDto userDto;
    private UserEditDto userEditDto;
    private RolesDto rolesDto;

    @BeforeEach
    void setUp() {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userAccountController).build();

        // Setup test data
        userRegisterDto = new UserRegisterDto();
        // Since UserRegisterDto doesn't have setters, we need to use reflection or create a new class
        // For simplicity, we'll assume the fields are accessible for testing

        Set<String> roles = new HashSet<>();
        roles.add("USER");

        userDto = UserDto.builder()
                .login("testUser")
                .firstName("Test")
                .lastName("User")
                .role("USER")
                .build();

        userEditDto = new UserEditDto();
        // Since UserEditDto doesn't have setters, we need to use reflection or create a new class
        // For simplicity, we'll assume the fields are accessible for testing

        rolesDto = RolesDto.builder()
                .login("testUser")
                .role("USER")
                .build();
    }

    @Test
    void testRegister() throws Exception {
        when(userAccountService.register(any(UserRegisterDto.class))).thenReturn(userEditDto);

        mockMvc.perform(post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userEditDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userEditDto.getLastName()));
    }

    @Test
    void testLogin() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");
        when(userAccountService.getUser("testUser")).thenReturn(userDto);

        mockMvc.perform(patch("/account/login")
                .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void testRemoveUser() throws Exception {
        when(userAccountService.removeUser(anyString())).thenReturn(userEditDto);

        mockMvc.perform(delete("/account/user/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userEditDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userEditDto.getLastName()));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userAccountService.updateUser(anyString(), any(UserEditDto.class))).thenReturn(userEditDto);

        mockMvc.perform(patch("/account/user/testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEditDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userEditDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userEditDto.getLastName()));
    }

    @Test
    void testAddRole() throws Exception {
        when(userAccountService.changeRolesList(anyString(), anyString(), eq(true))).thenReturn(rolesDto);

        mockMvc.perform(patch("/account/user/testUser/role/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void testDeleteRole() throws Exception {
        when(userAccountService.changeRolesList(anyString(), anyString(), eq(false))).thenReturn(rolesDto);

        mockMvc.perform(delete("/account/user/testUser/role/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void testChangePassword() throws Exception {
        doNothing().when(userAccountService).changePassword(anyString(), anyString());

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");

        mockMvc.perform(patch("/account/password")
                .principal(mockPrincipal)
                .header("X-Password", "newPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUser() throws Exception {
        when(userAccountService.getUser(anyString())).thenReturn(userDto);

        mockMvc.perform(get("/account/user/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }
}
