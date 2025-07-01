package ait.cohort5860.accouting.service;

import ait.cohort5860.accounting.dao.UserAccountRepository;
import ait.cohort5860.accounting.dto.RolesDto;
import ait.cohort5860.accounting.dto.UserDto;
import ait.cohort5860.accounting.dto.UserEditDto;
import ait.cohort5860.accounting.dto.UserRegisterDto;
import ait.cohort5860.accounting.dto.exception.UserExestisException;
import ait.cohort5860.accounting.dto.exception.UserNotFoundException;
import ait.cohort5860.accounting.model.Role;
import ait.cohort5860.accounting.model.UserAccount;
import ait.cohort5860.accounting.service.UserAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AccoutingServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    private UserAccount userAccount;
    private UserRegisterDto userRegisterDto;
    private UserDto userDto;
    private UserEditDto userEditDto;
    private RolesDto rolesDto;

    // Helper method to set field value using reflection
    private void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    @BeforeEach
    void setUp() {
        // Setup test data
        userAccount = spy(UserAccount.builder()
                .login("testUser")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .build());

        // Make sure addRole and removeRole methods work properly
        lenient().doReturn(true).when(userAccount).addRole(anyString());
        lenient().doReturn(true).when(userAccount).removeRole(anyString());

        userRegisterDto = new UserRegisterDto();
        setField(userRegisterDto, "login", "testUser");
        setField(userRegisterDto, "password", "password");
        setField(userRegisterDto, "firstName", "Test");
        setField(userRegisterDto, "lastName", "User");

        userDto = UserDto.builder()
                .login("testUser")
                .firstName("Test")
                .lastName("User")
                .roles(Set.of("USER"))
                .build();

        userEditDto = new UserEditDto();
        setField(userEditDto, "firstName", "Test");
        setField(userEditDto, "lastName", "User");

        rolesDto = RolesDto.builder()
                .login("testUser")
                .roles(Set.of("USER", "MODERATOR"))
                .build();
    }

    @Test
    void testRegister() {
        // Arrange
        when(userAccountRepository.existsById("testUser")).thenReturn(false);
        when(modelMapper.map(userRegisterDto, UserAccount.class)).thenReturn(userAccount);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        when(modelMapper.map(userAccount, UserEditDto.class)).thenReturn(userEditDto);

        // Act
        UserEditDto result = userAccountService.register(userRegisterDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());

        // Verify
        verify(userAccountRepository).existsById("testUser");
        verify(modelMapper).map(userRegisterDto, UserAccount.class);
        verify(passwordEncoder).encode("password");
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount, UserEditDto.class);
    }

    @Test
    void testRegister_UserExists() {
        // Arrange
        when(userAccountRepository.existsById("testUser")).thenReturn(true);

        // Act & Assert
        assertThrows(UserExestisException.class, () -> userAccountService.register(userRegisterDto));

        // Verify
        verify(userAccountRepository).existsById("testUser");
        verify(userAccountRepository, never()).save(any(UserAccount.class));
    }

    @Test
    void testGetUser() {
        // Arrange
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(modelMapper.map(userAccount, UserDto.class)).thenReturn(userDto);

        // Act
        UserDto result = userAccountService.getUser("testUser");

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertTrue(result.getRoles().contains("USER"));

        // Verify
        verify(userAccountRepository).findById("testUser");
        verify(modelMapper).map(userAccount, UserDto.class);
    }

    @Test
    void testGetUser_NotFound() {
        // Arrange
        when(userAccountRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userAccountService.getUser("nonExistentUser"));

        // Verify
        verify(userAccountRepository).findById("nonExistentUser");
    }

    @Test
    void testRemoveUser() {
        // Arrange
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(modelMapper.map(userAccount, UserEditDto.class)).thenReturn(userEditDto);

        // Act
        UserEditDto result = userAccountService.removeUser("testUser");

        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());

        // Verify
        verify(userAccountRepository).findById("testUser");
        verify(userAccountRepository).delete(userAccount);
        verify(modelMapper).map(userAccount, UserEditDto.class);
    }

    @Test
    void testUpdateUser() {
        // Arrange
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        when(modelMapper.map(userAccount, UserEditDto.class)).thenReturn(userEditDto);

        UserEditDto updateDto = new UserEditDto();
        setField(updateDto, "firstName", "Updated");
        setField(updateDto, "lastName", "Name");

        // Act
        UserEditDto result = userAccountService.updateUser("testUser", updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());

        // Verify
        verify(userAccountRepository).findById("testUser");
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount, UserEditDto.class);
    }

    @Test
    void testChangeRolesList_AddRole() {
        // Arrange
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        when(modelMapper.map(userAccount, RolesDto.class)).thenReturn(rolesDto);

        // Act
        RolesDto result = userAccountService.changeRolesList("testUser", "MODERATOR", true);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
        assertTrue(result.getRoles().contains("USER"));
        assertTrue(result.getRoles().contains("MODERATOR"));

        // Verify
        verify(userAccountRepository).findById("testUser");
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount, RolesDto.class);
    }

    @Test
    void testChangeRolesList_RemoveRole() {
        // Arrange
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        when(modelMapper.map(userAccount, RolesDto.class)).thenReturn(rolesDto);

        // Act
        RolesDto result = userAccountService.changeRolesList("testUser", "USER", false);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
        assertTrue(result.getRoles().contains("MODERATOR"));

        // Verify
        verify(userAccountRepository).findById("testUser");
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount, RolesDto.class);
    }

    @Test
    void testChangePassword() {
        // Arrange
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);

        // Act
        userAccountService.changePassword("testUser", "newPassword");

        // Assert
        assertEquals("newPassword", userAccount.getPassword());

        // Verify
        verify(userAccountRepository).findById("testUser");
        verify(userAccountRepository).save(userAccount);
    }
}
