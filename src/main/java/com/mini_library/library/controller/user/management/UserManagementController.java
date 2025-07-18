package com.mini_library.library.controller.user.management;

import com.mini_library.library.controller.book.request.BookRequest;
import com.mini_library.library.dto.BookDTO;
import com.mini_library.library.dto.BorrowRecordDTO;
import com.mini_library.library.dto.UserDTO;
import com.mini_library.library.model.Book;
import com.mini_library.library.service.book.BookService;
import com.mini_library.library.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users/management")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a ResponseEntity containing a list of UserDTO objects representing all users.
     *         Throws a runtime exception if no users are found.
     */
    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        final List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a single user based on the provided user ID.
     *
     * @param id the unique identifier of the user to fetch
     * @return a ResponseEntity containing the UserDTO object of the requested user
     */
    @GetMapping("/getone/{id}")
    public ResponseEntity<UserDTO> getOneUser(@PathVariable Long id) {
        return userService.getOneUser(id);
    }

    /**
     * Updates the details of an existing user identified by their ID.
     *
     * @param user the UserDTO object containing updated user information
     * @param idUser the ID of the user to be updated
     * @return a ResponseEntity containing the updated UserDTO object if the update is successful
     */
    @PutMapping("/update/{idUser}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO user, @PathVariable Long idUser) {
        return userService.updateUser(user, idUser);
    }

    /**
     * Deletes a user based on the specified user ID.
     * The user data is removed from the database.
     *
     * @param idUser the ID of the user to be deleted
     */
    @DeleteMapping("/delete/{idUser}")
    public void deleteUser(@PathVariable Long idUser) {
        userService.deleteUser(idUser);
    }
}

