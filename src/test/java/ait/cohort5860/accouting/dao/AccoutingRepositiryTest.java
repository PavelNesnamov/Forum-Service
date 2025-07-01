package ait.cohort5860.accouting.dao;

import ait.cohort5860.accounting.dao.UserAccountRepository;
import ait.cohort5860.accounting.model.Role;
import ait.cohort5860.accounting.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class tests the UserAccountRepository methods.
 * It uses Mockito to mock the repository behavior.
 */
@ExtendWith(MockitoExtension.class)
public class AccoutingRepositiryTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    private UserAccount user1;
    private UserAccount user2;
    private UserAccount user3;

    @BeforeEach
    void setUp() {
        // Create users with different roles
        user1 = UserAccount.builder()
                .login("user1")
                .password("password1")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();

        user2 = UserAccount.builder()
                .login("user2")
                .password("password2")
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.MODERATOR)
                .build();

        user3 = UserAccount.builder()
                .login("admin")
                .password("adminpass")
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMINISTRATOR)
                .build();
    }

    @Test
    void testBasicCrudOperations() {
        // Create a new user
        UserAccount newUser = UserAccount.builder()
                .login("newuser")
                .password("newpass")
                .firstName("New")
                .lastName("User")
                .role(Role.USER)
                .build();

        // Mock repository behavior for save, findById, findAll, and delete
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(newUser);
        when(userAccountRepository.findById("newuser")).thenReturn(Optional.of(newUser));
        when(userAccountRepository.findAll()).thenReturn(Arrays.asList(user1, user2, user3));
        doNothing().when(userAccountRepository).delete(any(UserAccount.class));

        // Test save
        UserAccount savedUser = userAccountRepository.save(newUser);
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getLogin());
        assertEquals("New", savedUser.getFirstName());

        // Test findById
        UserAccount foundUser = userAccountRepository.findById("newuser").orElse(null);
        assertNotNull(foundUser);
        assertEquals("newuser", foundUser.getLogin());
        assertEquals("New", foundUser.getFirstName());

        // Test update
        foundUser.setFirstName("Updated");
        when(userAccountRepository.save(foundUser)).thenReturn(foundUser);
        when(userAccountRepository.findById("newuser")).thenReturn(Optional.of(foundUser));

        UserAccount updatedUser = userAccountRepository.save(foundUser);
        assertEquals("Updated", updatedUser.getFirstName());

        UserAccount retrievedUser = userAccountRepository.findById("newuser").orElse(null);
        assertNotNull(retrievedUser);
        assertEquals("Updated", retrievedUser.getFirstName());

        // Test delete
        userAccountRepository.delete(foundUser);
        when(userAccountRepository.findById("newuser")).thenReturn(Optional.empty());
        assertTrue(userAccountRepository.findById("newuser").isEmpty());

        // Test findAll
        List<UserAccount> allUsers = userAccountRepository.findAll();
        assertEquals(3, allUsers.size());

        // Verify the repository methods were called
        verify(userAccountRepository, times(2)).save(any(UserAccount.class));
        verify(userAccountRepository, times(3)).findById("newuser");
        verify(userAccountRepository).delete(any(UserAccount.class));
        verify(userAccountRepository).findAll();
    }

    @Test
    void testUserWithMultipleRoles() {
        // Create a user with multiple roles directly
        UserAccount user = UserAccount.builder()
                .login("roleuser")
                .password("password")
                .firstName("Role")
                .lastName("User")
                .role(Role.USER)
                .role(Role.MODERATOR)
                .build();

        when(userAccountRepository.findById("roleuser")).thenReturn(Optional.of(user));

        // Test finding the user
        UserAccount foundUser = userAccountRepository.findById("roleuser").orElse(null);
        assertNotNull(foundUser);
        assertEquals(2, foundUser.getRoles().size());
        assertTrue(foundUser.getRoles().contains(Role.USER));
        assertTrue(foundUser.getRoles().contains(Role.MODERATOR));

        // Verify the repository methods were called
        verify(userAccountRepository).findById("roleuser");
    }

    @Test
    void testUserWithNoRoles() {
        // Create a user with no roles
        UserAccount user = UserAccount.builder()
                .login("noroleuser")
                .password("password")
                .firstName("NoRole")
                .lastName("User")
                .build();

        when(userAccountRepository.findById("noroleuser")).thenReturn(Optional.of(user));

        // Test finding the user
        UserAccount foundUser = userAccountRepository.findById("noroleuser").orElse(null);
        assertNotNull(foundUser);
        assertTrue(foundUser.getRoles().isEmpty());

        // Verify the repository methods were called
        verify(userAccountRepository).findById("noroleuser");
    }
}
