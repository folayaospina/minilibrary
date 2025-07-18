package com.mini_library.library.service.user;

import com.mini_library.library.dto.UserDTO;
import com.mini_library.library.interfaces.IRole;
import com.mini_library.library.model.Role;
import com.mini_library.library.model.User;
import com.mini_library.library.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @Test
    @DisplayName("Should exclude users with invalid or null fields when mapping to UserDTO")
    void getAllUsers_ShouldExcludeInvalidOrNullFields() {
        List<User> mockUsers = List.of(
                createMockUser(1L, "Valid User", "validuser@example.com"),
                createMockUser(2L, "", "userwithemaill@example.com"),
                createMockUser(3L, "UserWithInvalidEmail", "")
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Valid User");
    }

    @Test
    @DisplayName("Should handle large number of users correctly")
    void getAllUsers_ShouldHandleLargeNumberOfUsers() {
        List<User> mockUsers = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            mockUsers.add(createMockUser(i, "User" + i, "user" + i + "@example.com"));
        }

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1000);
        assertThat(result.get(0).getName()).isEqualTo("User1");
        assertThat(result.get(999).getName()).isEqualTo("User1000");
    }
    @Test
    @DisplayName("Should return all users when they exist")
    void getAllUsers_ShouldReturnUsers() {
        List<User> mockUsers = List.of(
                createMockUser(1L, "John Doe", "john@example.com"),
                createMockUser(2L, "Jane Doe", "jane@example.com")
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should throw exception when no users are found")
    void getAllUsers_ShouldThrowExceptionWhenNoUsersFound() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> userService.getAllUsers())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No users found");
    }

    private User createMockUser(Long id, String name, String email) {
        return createMockUserWithRole(id, name, email, IRole.USER.toString());
    }

    private User createMockUserWithRole(Long id, String name, String email, String role) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setRole(IRole.valueOf(role));
        user.setRole(userRole);

        return user;
    }


}