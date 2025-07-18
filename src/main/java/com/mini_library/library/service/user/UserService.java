package com.mini_library.library.service.user;

import com.mini_library.library.dto.UserDTO;
import com.mini_library.library.model.User;
import com.mini_library.library.repository.BookRepository;
import com.mini_library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public List<UserDTO> getAllUsers() {
        final List<User> users = userRepository.findAll();
        List<UserDTO> usersList = users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
        if (usersList.isEmpty()) {
            throw new RuntimeException("No users found");
        }
        return usersList;
    }

    @Transactional
    public ResponseEntity<UserDTO> getOneUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPassword().isEmpty()) {
            throw new RuntimeException("User doesnt exist");
        }
        
        UserDTO userDTO = UserDTO.fromUser(user);
        return ResponseEntity.ok(userDTO);
    }


    @Transactional
    public ResponseEntity<UserDTO> updateUser(UserDTO userDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        validateLibrarianRole(user);
        if (!userDTO.getName().isEmpty()) {
        user.setName(userDTO.getName());
        }else{
            user.setName(user.getName());
        }
        if (!userDTO.getEmail().isEmpty()) {
        user.setEmail(userDTO.getEmail());
        }else {
            user.setEmail(user.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(UserDTO.fromUser(updatedUser));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        validateLibrarianRole(user);
        userRepository.delete(user);
        ResponseEntity.ok("Usuario eliminado");
    }


    private void validateLibrarianRole(User user) {
        if (user.getRole().getRole().toString().equals("USER")) {
            throw new RuntimeException("El usuario no es bibliotecario");
        }
    }

}
